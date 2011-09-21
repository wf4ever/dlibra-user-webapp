/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

/**
 * @author Piotr Hołubowicz
 *
 */
public class AuthCodeData
{

	private String code;

	private String providedRedirectURI;

	private String userId;

	private String clientId;


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
	}


	public String getCode()
	{
		return code;
	}


	public String getProvidedRedirectURI()
	{
		return providedRedirectURI;
	}


	public String getUserId()
	{
		return userId;
	}


	public String getClientId()
	{
		return clientId;
	}

}
