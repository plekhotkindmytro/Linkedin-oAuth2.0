package dmytro.service.linkedin;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.oauth.OAuth20ServiceImpl;

public class OAuth20ThreeLeggedServiceImpl extends OAuth20ServiceImpl {

	private static final String DEFAULT_BEARER_NAME = "Bearer";
	private final String bearerName;
	private OAuthConfig config;

	/**
	 * Uses "Bearer" as bearer name for Authorization header
	 * 
	 * @param api
	 * @param config
	 */
	public OAuth20ThreeLeggedServiceImpl(DefaultApi20 api, OAuthConfig config) {
		this(api, config, DEFAULT_BEARER_NAME);
	}

	public OAuth20ThreeLeggedServiceImpl(DefaultApi20 api, OAuthConfig config, String bearerName) {
		super(api, config);
		this.config = config;
		this.bearerName = bearerName;
	}

	@Override
	public void signRequest(Token accessToken, OAuthRequest request) {
		switch (config.getSignatureType()) {
		case Header:
			config.log("using Http Header signature");
			request.addHeader(OAuthConstants.HEADER, bearerName + " " + accessToken.getToken());
			break;
		case QueryString:
			config.log("using Querystring signature");
			request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
			break;
		}
	}

}
