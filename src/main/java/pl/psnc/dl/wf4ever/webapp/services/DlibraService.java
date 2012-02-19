/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.AccessToken;
import pl.psnc.dl.wf4ever.webapp.model.AccessTokenList;
import pl.psnc.dl.wf4ever.webapp.model.OAuthClient;
import pl.psnc.dl.wf4ever.webapp.model.OAuthClientList;
import pl.psnc.dl.wf4ever.webapp.model.OpenIdUser;

/**
 * @author Piotr Hołubowicz
 * 
 */
public class DlibraService
{

	private static final Logger log = Logger.getLogger(DlibraService.class);

<<<<<<< HEAD
	public static final String DEFAULT_VERSION = "v1";

=======
>>>>>>> 2.0.0
	private static final String URI_SCHEME = "http";

	private static final String URI_HOST = "sandbox.wf4ever-project.org";

	private static final String URI_PATH_BASE = "/rosrs5";

	private static final String URI_USERS = URI_PATH_BASE + "/users";

	private static final String URI_USER_ID = URI_USERS + "/%s";

	private static final String URI_CLIENT = URI_PATH_BASE + "/clients";

	private static final String URI_CLIENT_ID = URI_CLIENT + "/%s";

	private static final String URI_ACCESS_TOKEN = URI_PATH_BASE + "/accesstokens";

	private static final String URI_ACCESS_TOKEN_ID = URI_PATH_BASE + "/accesstokens/%s";

	private static final Token WFADMIN_ACCESS_TOKEN = generateAccessToken("wfadmin", "wfadmin!!!");

	private static final OAuthService dLibraService = DlibraApi.getOAuthService();


	public static boolean userExistsInDlibra(String username)
	{
		String url = createUserIdURL(username).toString();
		try {
			OAuthHelpService.sendRequest(dLibraService, Verb.GET, url, WFADMIN_ACCESS_TOKEN);
			return true;
		}
		catch (OAuthException e) {
			return false;
		}
	}


	public static boolean createUser(String openId, String username)
		throws Exception
	{
		boolean created = true;

		String url = createUserIdURL(openId).toString();
		String payload = username != null && !username.isEmpty() ? username : openId;
		try {
			OAuthHelpService.sendRequest(dLibraService, Verb.PUT, url, WFADMIN_ACCESS_TOKEN, payload, "text/plain");
		}
		catch (OAuthException e) {
			if (e.getResponse().getCode() == HttpURLConnection.HTTP_CONFLICT) {
				log.warn("Registering a user that already exists in dLibra");
				created = false;
			}
			else {
				throw e;
			}
		}

		return created;
	}


	public static void deleteUser(OpenIdUser user)
		throws Exception
	{
		String url = createUserIdURL(user.getOpenId()).toString();
		OAuthHelpService.sendRequest(dLibraService, Verb.DELETE, url, WFADMIN_ACCESS_TOKEN);
	}


	public static OAuthClient getClient(String clientId)
		throws Exception
	{
		String url = createClientIdURL(clientId).toString();
		Response response = OAuthHelpService.sendRequest(dLibraService, Verb.GET, url, WFADMIN_ACCESS_TOKEN);
		return (OAuthClient) unmarshall(response.getBody(), OAuthClient.class);
	}


	public static List<OAuthClient> getClients()
		throws OAuthException, JAXBException
	{
		String url = getClientsURL().toString();
		Response response = OAuthHelpService.sendRequest(dLibraService, Verb.GET, url, WFADMIN_ACCESS_TOKEN);
		return ((OAuthClientList) unmarshall(response.getBody(), OAuthClientList.class)).getList();
	}


	public static String createAccessToken(String userId, String clientId)
		throws Exception
	{
		String url = createAccessTokenURL().toString();
		String payload = clientId + "\r\n" + userId;
		Response response = OAuthHelpService.sendRequest(dLibraService, Verb.POST, url, WFADMIN_ACCESS_TOKEN, payload,
			"text/plain");
		String at = response.getHeader("Location");
		if (at.indexOf('/') < 0) {
			throw new Exception("Invalid response: " + response.getBody());
		}
		return at.substring(at.lastIndexOf('/') + 1);
	}


<<<<<<< HEAD
	public static void sendResource(String path, String roName, byte[] bs,
			String contentType, DlibraUser user)
		throws Exception
	{
		String url = createResourceURL(user.getUsername(), roName,
			DEFAULT_VERSION, path).toString();
		OAuthRequest request = new OAuthRequest(Verb.PUT, url);
		request.addHeader("Content-Type", contentType != null ? contentType
				: "text/plain");
		request.addPayload(bs);
		dLibraService.signRequest(user.getDlibraAccessToken(), request);
		Response response = request.send();
		if (response.getCode() != HttpURLConnection.HTTP_OK) {
			throw new Exception("Error when sending resource " + path
					+ ", response: " + response.getCode() + " "
					+ response.getBody());
		}
=======
	public static List<AccessToken> getAccessTokens(String userId)
		throws OAuthException, JAXBException
	{
		String url = getAccessTokensURL(userId).toString();
		Response response = OAuthHelpService.sendRequest(dLibraService, Verb.GET, url, WFADMIN_ACCESS_TOKEN);
		return ((AccessTokenList) unmarshall(response.getBody(), AccessTokenList.class)).getList();
>>>>>>> 2.0.0
	}


	public static void deleteAccessToken(String token)
		throws Exception
	{
		String url = createAccessTokenIdURL(token).toString();
		OAuthHelpService.sendRequest(dLibraService, Verb.DELETE, url, WFADMIN_ACCESS_TOKEN);
	}


	private static Object unmarshall(String xml, Class< ? > resultClass)
		throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(resultClass);
		Unmarshaller u = jc.createUnmarshaller();
		StringBuffer xmlStr = new StringBuffer(xml);
		return u.unmarshal(new StreamSource(new StringReader(xmlStr.toString())));
	}


	private static Token generateAccessToken(String username, String password)
	{
		String token = Base64.encodeBase64String((username + ":" + password).getBytes());
		token = StringUtils.trim(token);
		log.debug(String.format("Username %s, password %s, access token %s", username, password, token));
		return new Token(token, null);
	}


	private static URL createUserIdURL(String userId)
	{
		try {
			String path = String.format(URI_USER_ID, Base64.encodeBase64URLSafeString(userId.getBytes()));
			return new URI(URI_SCHEME, URI_HOST, path, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL createClientIdURL(String clientId)
	{
		try {
			String path = String.format(URI_CLIENT_ID, clientId);
			return new URI(URI_SCHEME, URI_HOST, path, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL getClientsURL()
	{
		try {
			String path = URI_CLIENT;
			return new URI(URI_SCHEME, URI_HOST, path, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL createAccessTokenURL()
	{
		try {
			return new URI(URI_SCHEME, URI_HOST, URI_ACCESS_TOKEN, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL createAccessTokenIdURL(String accessToken)
	{
		try {
			String path = String.format(URI_ACCESS_TOKEN_ID, accessToken);
			return new URI(URI_SCHEME, URI_HOST, path, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL getAccessTokensURL(String userId)
	{
		try {
			userId = Base64.encodeBase64URLSafeString(userId.getBytes());
			return new URI(URI_SCHEME, URI_HOST, URI_ACCESS_TOKEN, "user_id=" + userId, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}

}
