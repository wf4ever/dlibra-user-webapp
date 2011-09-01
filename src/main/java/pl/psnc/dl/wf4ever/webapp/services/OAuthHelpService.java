/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.net.HttpURLConnection;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * @author Piotr Hołubowicz
 *
 */
public class OAuthHelpService
{

	public static Response sendRequest(OAuthService service, Verb verb,
			String url, Token token)
		throws OAuthException
	{
		OAuthRequest request = new OAuthRequest(verb, url);
		service.signRequest(token, request);
		Response response = request.send();
		validateResponseCode(verb, response);
		return response;
	}


	public static Response sendRequest(OAuthService service, Verb verb,
			String url, Token token, String payload)
		throws OAuthException
	{
		return sendRequest(service, verb, url, token, payload, "text/plain");
	}


	public static Response sendRequest(OAuthService service, Verb verb,
			String url, Token token, String payload, String contentType)
		throws OAuthException
	{
		OAuthRequest request = new OAuthRequest(verb, url);
		request.addPayload(payload);
		request.addHeader("Content-type", contentType);
		service.signRequest(token, request);
		Response response = request.send();
		validateResponseCode(verb, response);
		return response;
	}


	/**
	 * @param verb
	 * @param response
	 * @throws OAuthException
	 */
	private static void validateResponseCode(Verb verb, Response response)
		throws OAuthException
	{
		switch (verb) {
			case GET:
				if (response.getCode() != HttpURLConnection.HTTP_OK) {
					throw prepareException(response);
				}
				break;
			case PUT:
				if (response.getCode() != HttpURLConnection.HTTP_OK) {
					throw prepareException(response);
				}
				break;
			case POST:
				if (response.getCode() != HttpURLConnection.HTTP_CREATED) {
					throw prepareException(response);
				}
				break;
			case DELETE:
				if (response.getCode() != HttpURLConnection.HTTP_NO_CONTENT) {
					throw prepareException(response);
				}
				break;
		}
	}


	private static OAuthException prepareException(Response response)
	{
		if (response.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
			return new OAuthException(response,
					"the access token has been rejected");
		}
		return new OAuthException(response);
	}
}
