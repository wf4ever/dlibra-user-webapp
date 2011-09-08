/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.scribe.model.Token;

/**
 * @author Piotr Hołubowicz
 *
 */
@Entity
@Table(name = "users")
public class DlibraUser
	implements Serializable
{

	private static final long serialVersionUID = 3184540866229897863L;

	private String openId;

	private String username;

	private String password;

	private Token dLibraAccessToken;

	private OpenIdData openIdData;

	private String myExpToken;

	private String myExpSecret;


	public void setOpenId(String openId)
	{
		this.openId = openId;
	}


	/**
	 * @return the openID
	 */
	@Id
	public String getOpenId()
	{
		return openId;
	}


	/**
	 * @return the username
	 */
	@Basic
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
	 * @return the password
	 */
	@Basic
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


	/**
	 * @return the dLibra accessToken
	 */
	@Transient
	public Token getDlibraAccessToken()
	{
		return dLibraAccessToken;
	}


	/**
	 * @param token the accessToken to set
	 */
	public void setDlibraAccessToken(Token token)
	{
		this.dLibraAccessToken = token;
	}


	/**
	 * @return the public key of the dLibra access token
	 */
	@Basic
	public String getDlibraAccessTokenString()
	{
		return dLibraAccessToken != null ? dLibraAccessToken.getToken() : null;
	}


	/**
	 * 
	 * @param accessTokenString the public key of the dLibra access token
	 */
	public void setDlibraAccessTokenString(String accessTokenString)
	{
		this.dLibraAccessToken = new Token(accessTokenString, null);
	}


	@Transient
	public String getRegisterButtonText()
	{
		if (isRegistered()) {
			return "Unregister from dLibra";
		}
		else {
			return "Register in dLibra";
		}
	}


	@Transient
	public boolean isRegistered()
	{
		return this.dLibraAccessToken != null;
	}


	/**
	 * @return the openIdData
	 */
	@Transient
	public OpenIdData getOpenIdData()
	{
		return openIdData;
	}


	/**
	 * @param openIdData the openIdData to set
	 */
	public void setOpenIdData(OpenIdData openIdData)
	{
		this.openIdData = openIdData;
	}


	/**
	 * @return the myExpAccessToken
	 */
	@Transient
	public Token getMyExpAccessToken()
	{
		if (myExpToken == null || myExpSecret == null)
			return null;
		else
			return new Token(myExpToken, myExpSecret);
	}


	/**
	 * @param myExpAccessToken the myExpAccessToken to set
	 */
	public void setMyExpAccessToken(Token token)
	{
		if (token != null) {
			myExpToken = token.getToken();
			myExpSecret = token.getSecret();
		}
		else {
			myExpToken = null;
			myExpSecret = null;
		}
	}


	/**
	 * @return the myExpToken
	 */
	@Basic
	public String getMyExpToken()
	{
		return myExpToken;
	}


	/**
	 * @param myExpToken the myExpToken to set
	 */
	public void setMyExpToken(String myExpToken)
	{
		this.myExpToken = myExpToken;
	}


	/**
	 * @return the myExpSecret
	 */
	@Basic
	public String getMyExpSecret()
	{
		return myExpSecret;
	}


	/**
	 * @param myExpSecret the myExpSecret to set
	 */
	public void setMyExpSecret(String myExpSecret)
	{
		this.myExpSecret = myExpSecret;
	}

}
