/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;

import org.scribe.model.Token;

/**
 * @author Piotr Hołubowicz
 *
 */
public class DlibraUserModel
	implements Serializable
{

	private static final long serialVersionUID = 3184540866229897863L;

	private boolean authenticated = false;

	private String openId;

	private Token accessToken;

	private String myExpId;

	private OpenIdDataModel openIdData;

	private String username;

	private String password;


	public void setOpenId(String openId)
	{
		this.openId = openId;
	}


	/**
	 * @return the openID
	 */
	public String getOpenId()
	{
		return openId;
	}


	/**
	 * @return the accessToken
	 */
	public Token getAccessToken()
	{
		return accessToken;
	}


	/**
	 * @param token the accessToken to set
	 */
	public void setAccessToken(Token token)
	{
		this.accessToken = token;
	}


	public String getAccessTokenString()
	{
		return accessToken != null ? accessToken.getToken() : null;
	}


	public String getRegisterButtonText()
	{
		if (isRegistered()) {
			return "Unregister from dLibra";
		}
		else {
			return "Register in dLibra";
		}
	}


	public boolean isRegistered()
	{
		return this.accessToken != null;
	}


	/**
	 * @return the myExpId
	 */
	public String getMyExpId()
	{
		return myExpId;
	}


	/**
	 * @param myExpId the myExpId to set
	 */
	public void setMyExpId(String myExpId)
	{
		this.myExpId = myExpId;
	}


	/**
	 * @return the openIdData
	 */
	public OpenIdDataModel getOpenIdData()
	{
		return openIdData;
	}


	/**
	 * @param openIdData the openIdData to set
	 */
	public void setOpenIdData(OpenIdDataModel openIdData)
	{
		this.openIdData = openIdData;
	}


	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}


	/**
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}


	/**
	 * @return the authenticated
	 */
	public boolean isAuthenticated()
	{
		return authenticated;
	}


	/**
	 * @param authenticated the authenticated to set
	 */
	public void setAuthenticated(boolean authenticated)
	{
		this.authenticated = authenticated;
	}


	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}


	/**
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

}
