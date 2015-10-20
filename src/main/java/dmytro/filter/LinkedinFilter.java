package dmytro.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;

import dmytro.service.linkedin.LinkedinService;

/**
 * Servlet Filter implementation class LinkedinFilter
 */
public class LinkedinFilter implements Filter {

	private LinkedinService linkedinService;

	/**
	 * Default constructor.
	 */
	public LinkedinFilter() {
		linkedinService = new LinkedinService();

	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession();

		Token accessToken = (Token) session.getAttribute(OAuthConstants.ACCESS_TOKEN);
		if (accessToken == null) {

			String code = request.getParameter("code");
			if (code == null) {
				String authUrl = linkedinService.getAuthorizationUrl();

				// Store authUrl to check "state" query parameter to be the same
				// after response from authUrl
				// More info here: https://developer.linkedin.com/docs/oauth2
				session.setAttribute("authUrl", authUrl);

				httpResponse.sendRedirect(authUrl);
				return;
			} else {
				// Implement CSRF attack defense using state
				// More info here: https://developer.linkedin.com/docs/oauth2
				String state = request.getParameter("state");
				String authUrl = (String) session.getAttribute("authUrl");
				final boolean fromOriginalUser = authUrl.contains("state=" + state);

				if (fromOriginalUser) {
					accessToken = linkedinService.getAccessToken(code);
					session.setAttribute(OAuthConstants.ACCESS_TOKEN, accessToken);
					// pass the request along the filter chain
					chain.doFilter(request, response);
				} else {
					httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
			}
		} else {
			// pass the request along the filter chain
			chain.doFilter(request, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
