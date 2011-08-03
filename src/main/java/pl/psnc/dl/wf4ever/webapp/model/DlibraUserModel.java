/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;

/**
 * @author piotrhol
 *
 */
public class DlibraUserModel
	implements Serializable
{

	private static final long serialVersionUID = 3184540866229897863L;

	private String openID;

	private String accessToken;


	public DlibraUserModel(String openID)
	{
		super();
		this.openID = openID;
	}


	/**
	 * @return the openID
	 */
	public String getOpenID()
	{
		return openID;
	}


	/**
	 * @return the accessToken
	 */
	public String getAccessToken()
	{
		return accessToken;
	}


	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}


	public String getMessage()
	{
		if (isRegistered()) {
			return "You are registered in dLibra. "
					+ "Your access token is " + accessToken + ".";
		}
		else {
			return "You are not registered in dLibra. By registering you will receive "
					+ "an access token that will allow you to use dLibra.";
		}
	}


	public String getButtonText()
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

}
