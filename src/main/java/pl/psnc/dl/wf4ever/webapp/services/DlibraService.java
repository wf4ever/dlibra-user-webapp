/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.util.crypt.Base64;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;

/**
 * @author Piotr Hołubowicz
 *
 */
public class DlibraService
{

	private static final Logger log = Logger.getLogger(DlibraService.class);

	public static final String DEFAULT_VERSION = "v1";

	private static final String URI_SCHEME = "http";

	private static final String URI_HOST = "sandbox.wf4ever-project.org";

	private static final String URI_PATH_BASE = "/rosrs3";

	private static final String URI_WORKSPACE = URI_PATH_BASE + "/workspaces";

	private static final String URI_WORKSPACE_ID = URI_WORKSPACE + "/%s";

	private static final String URI_ROS = URI_WORKSPACE_ID + "/ROs";

	private static final String URI_RO_ID = URI_ROS + "/%s";

	private static final String URI_VERSION_ID = URI_RO_ID + "/%s";

	private static final String URI_RESOURCE = URI_VERSION_ID + "/%s";

	private static final int PASSWORD_LENGTH = 20;

	private static final int USERNAME_LENGTH = 20;

	private static final Token WFADMIN_ACCESS_TOKEN = generateAccessToken(
		"wfadmin", "wfadmin!!!");

	private static final OAuthService dLibraService = DlibraApi
			.getOAuthService();


	public static boolean userExistsInDlibra(String username)
	{
		String url = createWorkspaceIdURL(username).toString();
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		dLibraService.signRequest(WFADMIN_ACCESS_TOKEN, request);
		Response response = request.send();
		return response.getCode() == HttpURLConnection.HTTP_OK;
	}


	public static boolean createWorkspace(DlibraUser user)
		throws Exception
	{
		boolean created = true;

		String username = generateUsername(user);
		String password = generatePassword();
		Token token = generateAccessToken(username, password);

		String url = createWorkspaceURL().toString();
		OAuthRequest request = new OAuthRequest(Verb.POST, url);
		request.addHeader("Content-Type", "text/plain");
		request.addPayload(username + "\r\n" + password);
		dLibraService.signRequest(WFADMIN_ACCESS_TOKEN, request);
		Response response = request.send();
		if (response.getCode() != HttpURLConnection.HTTP_CREATED) {
			if (response.getCode() == HttpURLConnection.HTTP_CONFLICT) {
				log.warn("Registering a user that already exists in dLibra");
				created = false;
			}
			else {
				throw new Exception("Error when creating workspace, response: "
						+ response.getCode() + " " + response.getBody());
			}
		}

		user.setUsername(username);
		user.setPassword(password);
		user.setDlibraAccessToken(token);

		HibernateService.storeUser(user);
		return created;
	}


	public static void deleteWorkspace(DlibraUser user)
		throws Exception
	{
		String url = createWorkspaceIdURL(user.getUsername()).toString();
		OAuthRequest request = new OAuthRequest(Verb.DELETE, url);
		dLibraService.signRequest(WFADMIN_ACCESS_TOKEN, request);
		Response response = request.send();

		user.setDlibraAccessToken(null);
		HibernateService.deleteUser(user);

		if (response.getCode() != HttpURLConnection.HTTP_CONFLICT) {
			throw new Exception("Error when deleting workspace, response: "
					+ response.getCode() + " " + response.getBody());
		}
	}


	public static boolean createResearchObjectAndVersion(String name,
			DlibraUser model, boolean ignoreIfExists)
		throws Exception
	{
		return createResearchObject(name, model, ignoreIfExists)
				&& createVersion(name, model, ignoreIfExists);
	}


	/**
	 * Creates a Research Object.
	 * @param name RO identifier
	 * @param user dLibra user model
	 * @param ignoreIfExists should it finish without throwing exception if ROSRS returns 409?
	 * @return true only if ROSRS returns 201 Created
	 * @throws Exception if ROSRS doesn't return 201 Created (or 409 if ignoreIfExists is true)
	 */
	public static boolean createResearchObject(String name, DlibraUser user,
			boolean ignoreIfExists)
		throws Exception
	{
		String url = createROsURL(user.getUsername()).toString();
		OAuthRequest request = new OAuthRequest(Verb.POST, url);
		request.addHeader("Content-type", "text/plain");
		request.addPayload(name);
		dLibraService.signRequest(user.getDlibraAccessToken(), request);
		Response response = request.send();
		if (response.getCode() == HttpURLConnection.HTTP_CREATED) {
			return true;
		}
		else if (response.getCode() == HttpURLConnection.HTTP_CONFLICT
				&& ignoreIfExists) {
			return false;
		}
		else {
			throw new Exception("Error when creating RO " + name
					+ ", response: " + response.getCode() + " "
					+ response.getBody());
		}
	}


	/**
	 * Creates a version "main" in a RO.
	 * @param roName RO identifier
	 * @param user dLibra user model
	 * @param ignoreIfExists should it finish without throwing exception if ROSRS returns 409?
	 * @return true only if ROSRS returns 201 Created
	 * @throws Exception if ROSRS doesn't return 201 Created (or 409 if ignoreIfExists is true)
	 */
	public static boolean createVersion(String roName, DlibraUser user,
			boolean ignoreIfExists)
		throws Exception
	{
		String url = createROIdURL(user.getUsername(), roName).toString();
		OAuthRequest request = new OAuthRequest(Verb.POST, url);
		request.addHeader("Content-type", "text/plain");
		request.addPayload(DEFAULT_VERSION);
		dLibraService.signRequest(user.getDlibraAccessToken(), request);
		Response response = request.send();
		if (response.getCode() == HttpURLConnection.HTTP_CREATED) {
			return true;
		}
		else if (response.getCode() == HttpURLConnection.HTTP_CONFLICT
				&& ignoreIfExists) {
			return false;
		}
		else {
			throw new Exception("Error when creating version, response: "
					+ response.getCode() + " " + response.getBody());
		}
	}


	public static void sendResource(String path, String roName, String content,
			String contentType, DlibraUser user)
		throws Exception
	{
		String url = createResourceURL(user.getUsername(), roName,
			DEFAULT_VERSION, path).toString();
		OAuthRequest request = new OAuthRequest(Verb.PUT, url);
		request.addHeader("Content-Type", contentType != null ? contentType
				: "text/plain");
		request.addPayload(content);
		dLibraService.signRequest(user.getDlibraAccessToken(), request);
		Response response = request.send();
		if (response.getCode() != HttpURLConnection.HTTP_OK) {
			throw new Exception("Error when sending resource " + path
					+ ", response: " + response.getCode() + " "
					+ response.getBody());
		}
	}


	private static String generatePassword()
	{
		return UUID.randomUUID().toString().substring(0, PASSWORD_LENGTH);
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


	public static Token generateAccessToken(String username, String password)
	{
		String token = Base64.encodeBase64String((username + ":" + password)
				.getBytes());
		token = StringUtils.trim(token);
		log.debug(String.format("Username %s, password %s, access token %s",
			username, password, token));
		return new Token(token, null);
	}


	private static URL createWorkspaceURL()
	{
		try {
			return new URI(URI_SCHEME, URI_HOST, URI_WORKSPACE, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL createWorkspaceIdURL(String workspaceId)
	{
		try {
			String path = String.format(URI_WORKSPACE_ID, workspaceId);
			return new URI(URI_SCHEME, URI_HOST, path, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL createROsURL(String workspaceId)
	{
		try {
			String path = String.format(URI_ROS, workspaceId);
			return new URI(URI_SCHEME, URI_HOST, path, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL createROIdURL(String workspaceId, String roId)
	{
		try {
			String path = String.format(URI_RO_ID, workspaceId, roId);
			return new URI(URI_SCHEME, URI_HOST, path, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL createResourceURL(String workspaceId, String roId,
			String versionId, String resource)
	{
		try {
			String path = String.format(URI_RESOURCE, workspaceId, roId, DEFAULT_VERSION,
				resource);
			return new URI(URI_SCHEME, URI_HOST, path, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}

}
