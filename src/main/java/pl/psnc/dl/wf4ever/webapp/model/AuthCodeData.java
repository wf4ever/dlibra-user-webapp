/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Piotr Hołubowicz
 *
 */
@Entity
@Table(name = "tempAuthCodeData")
public class AuthCodeData
{

	private String code;

	private String providedRedirectURI;

	private String userId;

	private String clientId;

	private Date created;


	public AuthCodeData()
	{

	}


	/**
	 * @param code
	 * @param clientRedirectURI May not be null, used for redirection
	 * @param providedRedirectURI May be null, if not than it is validated by access token endpoint
	 * @param userId
	 * @param clientId
	 */
	public AuthCodeData(String code, String providedRedirectURI, String userId,
			String clientId)
	{
		this.code = code;
		this.providedRedirectURI = providedRedirectURI;
		this.userId = userId;
		this.clientId = clientId;
		this.created = new Date();
	}


	@Id
	public String getCode()
	{
		return code;
	}


	public void setCode(String code)
	{
		this.code = code;
	}


	public String getProvidedRedirectURI()
	{
		return providedRedirectURI;
	}


	public void setProvidedRedirectURI(String providedRedirectURI)
	{
		this.providedRedirectURI = providedRedirectURI;
	}


	@Basic
	public String getUserId()
	{
		return userId;
	}


	public void setUserId(String userId)
	{
		this.userId = userId;
	}


	@Basic
	public String getClientId()
	{
		return clientId;
	}


	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}


	/**
	 * @return the created
	 */
	@Basic
	public Date getCreated()
	{
		return created;
	}


	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created)
	{
		this.created = created;
	}

}
