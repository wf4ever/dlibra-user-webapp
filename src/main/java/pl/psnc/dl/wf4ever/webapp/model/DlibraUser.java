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

	private OpenIdData openIdData;


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
		return username != null;
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

}
