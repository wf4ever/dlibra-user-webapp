/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.net.URI;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUserModel;

/**
 * @author Piotr Hołubowicz
 *
 */
public class DlibraService
{

	private static final Logger log = Logger.getLogger(DlibraService.class);

	private static final String ROSRS_SCHEME = "http";

	private static final String ROSRS_HOST = "calatola.man.poznan.pl";

	private static final int ROSRS_PORT = 80;

	private static final String ROSRS_PATH = "/rosrs3";

	private static final int PASSWORD_LENGTH = 20;

	private static final int USERNAME_LENGTH = 20;

	private static DefaultHttpClient client;

	static {
		initConnection();
	}


	private static boolean userExistsInDlibra(String username)
	{
		try {
			URI uri = new URI(ROSRS_SCHEME, null, ROSRS_HOST, ROSRS_PORT,
					ROSRS_PATH + "/workspaces/" + username, null, null);
			HttpGet get = new HttpGet(uri);
			HttpResponse response = client.execute(get);
			EntityUtils.consume(response.getEntity());
			log.debug("Response: " + response.getStatusLine().getStatusCode()
					+ " " + response.getStatusLine().getReasonPhrase());
			return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
		}
		catch (Exception e) {
			log.error(e);
			return false;
		}
	}


	public static void createWorkspace(DlibraUserModel model)
	{
		String username = generateUsername();
		String password = generatePassword();

		if (userExistsInDlibra(username)) {
			log.error("Duplicate username generated!");
			return;
		}

		try {
			URI uri = new URI(ROSRS_SCHEME, null, ROSRS_HOST, ROSRS_PORT,
					ROSRS_PATH + "/workspaces", null, null);
			HttpPost post = new HttpPost(uri);
			post.setEntity(new StringEntity(username + "\n" + password));
			HttpResponse response = client.execute(post);
			EntityUtils.consume(response.getEntity());
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
				log.error("Response: "
						+ response.getStatusLine().getStatusCode() + " "
						+ response.getStatusLine().getReasonPhrase());
			}
		}
		catch (Exception e) {
			log.error(e);
		}

		try {
			DerbyService.insertUser(model.getOpenId(), username, password);
			model.setAccessToken(DerbyService.getAccessToken(model.getOpenId()));
		}
		catch (SQLException e1) {
			log.error("Error when inserting username and password", e1);
		}
	}


	private static String generatePassword()
	{
		return UUID.randomUUID().toString().substring(0, PASSWORD_LENGTH);
	}


	private static String generateUsername()
	{
		Date now = new Date();
		String base = "openID" + now.getTime();
		return base.substring(0, Math.min(USERNAME_LENGTH, base.length()));
	}


	public static void deleteWorkspace(DlibraUserModel model)
	{
		String username;
		try {
			username = DerbyService.getUsername(model.getOpenId());
		}
		catch (IllegalArgumentException e) {
			log.error("Deleting workspace for openID " + model.getOpenId()
					+ " but not found");
			return;
		}

		try {
			URI uri = new URI(ROSRS_SCHEME, null, ROSRS_HOST, ROSRS_PORT,
					ROSRS_PATH + "/workspaces/" + username, null, null);
			HttpDelete delete = new HttpDelete(uri);
			HttpResponse response = client.execute(delete);
			EntityUtils.consume(response.getEntity());
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
				log.error("Response: "
						+ response.getStatusLine().getStatusCode() + " "
						+ response.getStatusLine().getReasonPhrase());
			}
		}
		catch (Exception e) {
			log.error(e);
		}
		try {
			DerbyService.deleteUser(model.getOpenId());
		}
		catch (SQLException e) {
			log.error("Error when deleting username and password", e);
		}
		model.setAccessToken(null);
	}


	private static void initConnection()
	{
		client = new DefaultHttpClient(new ThreadSafeClientConnManager());
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
				"wfadmin", "wfadmin!!!");
		client.getCredentialsProvider().setCredentials(
			new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
	}


	public static void provisionAuthenticatedUserModel(DlibraUserModel model)
	{
		if (DerbyService.userExists(model.getOpenId())) {
			model.setAccessToken(DerbyService.getAccessToken(model.getOpenId()));
		}
	}

}
