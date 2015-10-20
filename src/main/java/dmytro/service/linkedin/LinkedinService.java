package dmytro.service.linkedin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class LinkedinService {
	private static final Token EMPTY_REQUEST_TOKEN = null;
	private final String apiKey;
	private final String apiSecret;
	private final String oAuthCallback;
	private OAuthService oAuthService;

	public LinkedinService() {
		Properties properties = new Properties();
		InputStream in;
		try {
			in = LinkedinService.class.getResourceAsStream("/linkedin.properties");
			properties.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		apiKey = properties.getProperty("api_key");
		apiSecret = properties.getProperty("api_secret");
		oAuthCallback = properties.getProperty("oauth_callback");
		oAuthService = new ServiceBuilder().provider(LinkedInApi20.class).apiKey(apiKey).apiSecret(apiSecret).callback(oAuthCallback).signatureType(SignatureType.Header).build();
	}

	public String getAuthorizationUrl() {
		return oAuthService.getAuthorizationUrl(EMPTY_REQUEST_TOKEN);
	}

	public Token getAccessToken(String code) {
		Verifier verifier = new Verifier(code);
		return oAuthService.getAccessToken(EMPTY_REQUEST_TOKEN, verifier);
	}

	public void signRequest(Token accessToken, OAuthRequest oauthRequest) {
		oAuthService.signRequest(accessToken, oauthRequest);
	}

	public String getBasicUserInfo(Token accessToken) {
		OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, "https://api.linkedin.com/v1/people/~?format=json");
		oAuthService.signRequest(accessToken, oauthRequest);
		Response oauthResponse = oauthRequest.send();
		String responseBody = oauthResponse.getBody();
		return responseBody;

	}

}
