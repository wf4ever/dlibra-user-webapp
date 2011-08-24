/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.oauth.OAuthService;

/**
 * @author Piotr Hołubowicz
 *
 */
public class DlibraApi
	extends DefaultApi20
{

	private static final String CONSUMER_KEY = "foo";

	private static final String SHARED_SECRET = "bar";


	public static OAuthService getOAuthService()
	{
		return new ServiceBuilder().provider(DlibraApi.class)
				.apiKey(DlibraApi.CONSUMER_KEY)
				.apiSecret(DlibraApi.SHARED_SECRET).build();
	}


	/* (non-Javadoc)
	 * @see org.scribe.builder.api.DefaultApi10a#getAccessTokenEndpoint()
	 */
	@Override
	public String getAccessTokenEndpoint()
	{
		return "foobar";
	}


	@Override
	public String getAuthorizationUrl(OAuthConfig config)
	{
		return null;
	}

}
