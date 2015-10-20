package dmytro;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.scribe.model.OAuthConstants;
import org.scribe.model.Token;

import dmytro.service.linkedin.LinkedinService;

/**
 * Servlet implementation class MainServlet
 */
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	LinkedinService linkedinService;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainServlet() {
		super();
		linkedinService = new LinkedinService();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Token accessToken = (Token) session.getAttribute(OAuthConstants.ACCESS_TOKEN);

		String userInfo = linkedinService.getBasicUserInfo(accessToken);

		response.getWriter().append(userInfo);
	}
}
