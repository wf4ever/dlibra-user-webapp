/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.util.crypt.Base64;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.OAuthClient;

/**
 * @author Piotr Hołubowicz
 *
 */
public class DlibraService
{

	private static final Logger log = Logger.getLogger(DlibraService.class);

	private static final String URI_SCHEME = "http";

	private static final String URI_HOST = "sandbox.wf4ever-project.org";

	private static final String URI_PATH_BASE = "/rosrs4";

	private static final String URI_USERS = URI_PATH_BASE + "/users";

	private static final String URI_USER_ID = URI_USERS + "/%s";

	private static final String URI_CLIENT = URI_PATH_BASE + "/clients";

	private static final String URI_CLIENT_ID = URI_CLIENT + "/%s";

	private static final String URI_ACCESS_TOKEN = URI_PATH_BASE
			+ "/accesstoken";

	private static final int USERNAME_LENGTH = 20;

	private static final Token WFADMIN_ACCESS_TOKEN = generateAccessToken(
		"wfadmin", "wfadmin!!!");

	private static final OAuthService dLibraService = DlibraApi
			.getOAuthService();


	public static boolean userExistsInDlibra(String username)
	{
		String url = createUserIdURL(username).toString();
		try {
			OAuthHelpService.sendRequest(dLibraService, Verb.GET, url,
				WFADMIN_ACCESS_TOKEN);
			return true;
		}
		catch (OAuthException e) {
			return false;
		}
	}


	public static boolean createUser(DlibraUser user)
		throws Exception
	{
		boolean created = true;

		String username = generateUsername(user);

		String url = createUsersURL().toString();
		String payload = username;
		try {
			OAuthHelpService.sendRequest(dLibraService, Verb.POST, url,
				WFADMIN_ACCESS_TOKEN, payload, "text/plain");
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

		user.setUsername(username);
		HibernateService.storeUser(user);
		return created;
	}


	public static void deleteUser(DlibraUser user)
		throws Exception
	{
		String url = createUserIdURL(user.getUsername()).toString();
		try {
			OAuthHelpService.sendRequest(dLibraService, Verb.DELETE, url,
				WFADMIN_ACCESS_TOKEN);
		}
		finally {
			user.setUsername(null);
			HibernateService.deleteUser(user);
		}
	}


	public static OAuthClient getClient(String clientId)
		throws Exception
	{
		String url = createClientIdURL(clientId).toString();
		Response response = OAuthHelpService.sendRequest(dLibraService,
			Verb.GET, url, WFADMIN_ACCESS_TOKEN);
		return createClient(response.getBody());
	}


	public static String getAccessToken(String userId, String clientId)
		throws Exception
	{
		String url = createAccessTokenURL().toString();
		String payload = clientId + "\r\n" + userId;
		Response response = OAuthHelpService.sendRequest(dLibraService,
			Verb.POST, url, WFADMIN_ACCESS_TOKEN, payload, "text/plain");
		String at = response.getHeader("Location");
		if (at.indexOf('/') < 0) {
			throw new Exception("Invalid response: " + response.getBody());
		}
		return at.substring(at.lastIndexOf('/') + 1);
	}


	private static OAuthClient createClient(String xml)
		throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(OAuthClient.class);

		Unmarshaller u = jc.createUnmarshaller();
		StringBuffer xmlStr = new StringBuffer(xml);
		return (OAuthClient) u.unmarshal(new StreamSource(new StringReader(
				xmlStr.toString())));
	}


	private static String generateUsername(DlibraUser model)
	{
		if (model.getOpenId().length() <= USERNAME_LENGTH) {
			return model.getOpenId();
		}
		if (model.getOpenIdData() != null
				&& model.getOpenIdData().getEmailAddress() != null) {
			return StringUtils
					.left(model.getOpenIdData().getEmailAddress(),
						USERNAME_LENGTH).replaceAll("\\s", "_")
					.replaceAll("\\W", "");
		}
		return "OpenID-" + new Date().getTime();
	}


	private static Token generateAccessToken(String username, String password)
	{
		String token = Base64.encodeBase64String((username + ":" + password)
				.getBytes());
		token = StringUtils.trim(token);
		log.debug(String.format("Username %s, password %s, access token %s",
			username, password, token));
		return new Token(token, null);
	}


	private static URL createUsersURL()
	{
		try {
			return new URI(URI_SCHEME, URI_HOST, URI_USERS, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL createUserIdURL(String userId)
	{
		try {
			String path = String.format(URI_USER_ID, userId);
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


	private static URL createAccessTokenURL()
	{
		try {
			return new URI(URI_SCHEME, URI_HOST, URI_ACCESS_TOKEN, null)
					.toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}
}
