/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import net.oauth.ConsumerProperties;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

/**
 * @author piotrhol
 *
 */
public class OAuthService
{

	private static ConsumerProperties consumers = null;
	
	private static OAuthClient client = new OAuthClient(new HttpClient4());


	public static void getAuthorization(String consumerName, PageParameters pageParameters,
			String oauthCallbackURL, String oauthFinishedURL)
		throws IOException, OAuthException, URISyntaxException
	{
		OAuthConsumer consumer = getConsumer(consumerName);
		OAuthAccessor accessProps = getAccessor(pageParameters,
			consumer, oauthCallbackURL);
	}


	public static OAuthConsumer getConsumer(String name)
		throws IOException
	{
		synchronized (OAuthService.class) {
			if (consumers == null) {
				Properties properties = new Properties();
				properties.load(OAuthService.class.getClassLoader()
						.getResourceAsStream("consumer.properties"));
				consumers = new ConsumerProperties(properties);
			}
		}
		//		if (context != null) {
		//			synchronized (consumerProperties) {
		//				String key = name + ".callbackURL";
		//				String value = consumerProperties.getProperty(key);
		//				if (value == null) {
		//					// Compute the callbackURL from the servlet context.
		//					URL resource = context.getResource(Callback.PATH);
		//					if (resource != null) {
		//						value = resource.toExternalForm();
		//					}
		//					else {
		//						value = Callback.PATH;
		//					}
		//					consumerProperties.setProperty(key, value);
		//				}
		//			}
		//		}
		return consumers.getConsumer(name);
	}


	public static OAuthAccessor getAccessor(PageParameters pageParameters,
			OAuthConsumer consumer, String oauthCallbackURL) throws IOException, OAuthException, URISyntaxException
	{
		OAuthAccessor accessor = new OAuthAccessor(consumer);
		String consumerName = (String) consumer.getProperty("name");
        List<OAuth.Parameter> parameters = OAuth.newList(OAuth.OAUTH_CALLBACK, oauthCallbackURL);
        OAuthMessage response = client.getRequestTokenResponse(accessor, null, parameters);
        
        String authorizationURL = accessor.consumer.serviceProvider.userAuthorizationURL;
        if (authorizationURL.startsWith("/")) {
            authorizationURL = (new URL(new URL(request.getRequestURL()
                    .toString()), request.getContextPath() + authorizationURL))
                    .toString();
        }
        authorizationURL = OAuth.addParameters(authorizationURL //
                , OAuth.OAUTH_TOKEN, accessor.requestToken);
        if (response.getParameter(OAuth.OAUTH_CALLBACK_CONFIRMED) == null) {
            authorizationURL = OAuth.addParameters(authorizationURL //
                    , OAuth.OAUTH_CALLBACK, callbackURL);
        }
		return accessor;
	}


}
