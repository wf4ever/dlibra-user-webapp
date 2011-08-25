/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.wicket.util.crypt.Base64;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUserModel;

/**
 * @author Piotr Hołubowicz
 *
 */
public class DlibraService
{

	private static final Logger log = Logger.getLogger(DlibraService.class);

	public static final String DEFAULT_VERSION = "main";

	private static final String URI_BASE = "http://sandbox.wf4ever-project.org:80/rosrs3";

	private static final String URI_WORKSPACE = URI_BASE + "/workspaces";

	private static final String URI_WORKSPACE_ID = URI_WORKSPACE + "/%s";

	private static final String URI_ROS = URI_WORKSPACE_ID + "/ROs";

	private static final String URI_RO_ID = URI_ROS + "/%s";

	private static final String URI_VERSION_ID = URI_RO_ID + "/%s";

	private static final String URI_RESOURCE = URI_VERSION_ID + "/%s";

	private static final int PASSWORD_LENGTH = 20;

	private static final int USERNAME_LENGTH = 20;

	private static final Token WFADMIN_ACCESS_TOKEN = new Token(
			Base64.encodeBase64String("wfadmin:wfadmin!!!".getBytes()), null);

	private static final OAuthService dLibraService = DlibraApi
			.getOAuthService();


	private static boolean userExistsInDlibra(String username)
	{
		String url = String.format(URI_WORKSPACE_ID, username);
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		dLibraService.signRequest(WFADMIN_ACCESS_TOKEN, request);
		Response response = request.send();
		return response.getCode() == HttpStatus.SC_OK;
	}


	public static void createWorkspace(DlibraUserModel model)
	{
		String username = generateUsername();
		String password = generatePassword();

		if (userExistsInDlibra(username)) {
			log.error("Duplicate username generated!");
			return;
		}

		String url = String.format(URI_WORKSPACE);
		OAuthRequest request = new OAuthRequest(Verb.POST, url);
		dLibraService.signRequest(WFADMIN_ACCESS_TOKEN, request);
		Response response = request.send();
		if (response.getCode() != HttpStatus.SC_CREATED) {
			log.error("Error when creating workspace, response: "
					+ response.getCode() + " " + response.getBody());
		}

		try {
			DerbyService.insertUser(model.getOpenId(), username, password);
			provisionAuthenticatedUserModel(model);
		}
		catch (SQLException e1) {
			log.error("Error when inserting username and password", e1);
		}
	}


	public static boolean createResearchObjectAndVersion(String name,
			DlibraUserModel model, boolean ignoreIfExists)
		throws Exception
	{
		return createResearchObject(name, model, ignoreIfExists)
				&& createVersion(name, model, ignoreIfExists);
	}


	/**
	 * Creates a Research Object.
	 * @param name RO identifier
	 * @param model dLibra user model
	 * @param ignoreIfExists should it finish without throwing exception if ROSRS returns 409?
	 * @return true only if ROSRS returns 201 Created
	 * @throws Exception if ROSRS doesn't return 201 Created (or 409 if ignoreIfExists is true)
	 */
	public static boolean createResearchObject(String name,
			DlibraUserModel model, boolean ignoreIfExists)
		throws Exception
	{
		String url = String.format(URI_ROS, model.getUsername());
		OAuthRequest request = new OAuthRequest(Verb.POST, url);
		request.addHeader("Content-type", "text/plain");
		request.addPayload(name);
		dLibraService.signRequest(new Token(model.getAccessToken(), null),
			request);
		Response response = request.send();
		if (response.getCode() == HttpStatus.SC_CREATED) {
			return true;
		}
		else if (response.getCode() == HttpStatus.SC_CONFLICT && ignoreIfExists) {
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
	 * @param model dLibra user model
	 * @param ignoreIfExists should it finish without throwing exception if ROSRS returns 409?
	 * @return true only if ROSRS returns 201 Created
	 * @throws Exception if ROSRS doesn't return 201 Created (or 409 if ignoreIfExists is true)
	 */
	public static boolean createVersion(String roName, DlibraUserModel model,
			boolean ignoreIfExists)
		throws Exception
	{
		String url = String.format(URI_RO_ID, model.getUsername(), roName);
		OAuthRequest request = new OAuthRequest(Verb.POST, url);
		request.addHeader("Content-type", "text/plain");
		request.addPayload(DEFAULT_VERSION);
		dLibraService.signRequest(new Token(model.getAccessToken(), null),
			request);
		Response response = request.send();
		if (response.getCode() == HttpStatus.SC_CREATED) {
			return true;
		}
		else if (response.getCode() == HttpStatus.SC_CONFLICT && ignoreIfExists) {
			return false;
		}
		else {
			throw new Exception("Error when creating version, response: "
					+ response.getCode() + " " + response.getBody());
		}
	}


	public static void sendResource(String path, String roName, String content,
			String contentType, DlibraUserModel model)
		throws Exception
	{
		String url = String.format(URI_RESOURCE, model.getUsername(), roName,
			DEFAULT_VERSION, path);
		OAuthRequest request = new OAuthRequest(Verb.PUT, url);
		request.addHeader("Content-Type", contentType != null ? contentType
				: "text/plain");
		request.addPayload(content);
		dLibraService.signRequest(new Token(model.getAccessToken(), null),
			request);
		Response response = request.send();
		if (response.getCode() != HttpStatus.SC_OK) {
			throw new Exception("Error when sending resource " + path
					+ ", response: " + response.getCode() + " "
					+ response.getBody());
		}
	}


	private static String generatePassword()
	{
		return UUID.randomUUID().toString().substring(0, PASSWORD_LENGTH);
	}


	private static String generateUsername()
	{
		Date now = new Date();
		return StringUtils.left("openID-" + now.getTime(), USERNAME_LENGTH);
	}


	public static void deleteWorkspace(DlibraUserModel model)
	{
		String url = String.format(URI_WORKSPACE_ID, model.getUsername());
		OAuthRequest request = new OAuthRequest(Verb.DELETE, url);
		dLibraService.signRequest(new Token(model.getAccessToken(), null),
			request);
		Response response = request.send();
		if (response.getCode() != HttpStatus.SC_OK) {
			log.error("Error when deleting workspace, response: "
					+ response.getCode() + " " + response.getBody());
		}

		try {
			DerbyService.deleteUser(model.getOpenId());
		}
		catch (SQLException e) {
			log.error("Error when deleting username and password", e);
		}
		model.setAccessToken(null);
	}


	public static void provisionAuthenticatedUserModel(DlibraUserModel model)
	{
		if (DerbyService.userExists(model.getOpenId())) {
			model.setAccessToken(DerbyService.getAccessToken(model.getOpenId()));
			model.setUsername(DerbyService.getUsername(model.getOpenId()));
		}
	}

}
