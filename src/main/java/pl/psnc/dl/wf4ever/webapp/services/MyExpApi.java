/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

/**
 * @author piotrhol
 *
 */
public class MyExpApi
	extends DefaultApi10a
{

	private static final String CONSUMER_KEY = "kA9xe84b4sGDDcLfMCBrw";

	private static final String SHARED_SECRET = "IqywoxpYNsWgz8seUONNgxnhohziFvjdsqZHdsQE";


	public static OAuthService getOAuthService(String oauthCallbackURL)
	{
		return new ServiceBuilder().provider(MyExpApi.class)
				.apiKey(MyExpApi.CONSUMER_KEY)
				.apiSecret(MyExpApi.SHARED_SECRET).callback(oauthCallbackURL)
				.build();
	}


	/* (non-Javadoc)
	 * @see org.scribe.builder.api.DefaultApi10a#getAccessTokenEndpoint()
	 */
	@Override
	public String getAccessTokenEndpoint()
	{
		return "http://www.myexperiment.org/oauth/access_token";
	}


	/* (non-Javadoc)
	 * @see org.scribe.builder.api.DefaultApi10a#getAuthorizationUrl(org.scribe.model.Token)
	 */
	@Override
	public String getAuthorizationUrl(Token arg0)
	{
		return "http://www.myexperiment.org/oauth/authorize";
	}


	/* (non-Javadoc)
	 * @see org.scribe.builder.api.DefaultApi10a#getRequestTokenEndpoint()
	 */
	@Override
	public String getRequestTokenEndpoint()
	{
		return "http://www.myexperiment.org/oauth/request_token";
	}

}
