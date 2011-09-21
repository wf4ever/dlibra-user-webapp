/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.pages;

/**
 * @author Piotr Hołubowicz
 *
 */
public class AuthCodeData
{

	private String code;

	private String redirectURI;

	private String userId;

	private String clientId;


	/**
	 * @param code
	 * @param redirectURI
	 * @param userId
	 * @param clientId
	 */
	public AuthCodeData(String code, String redirectURI, String userId,
			String clientId)
	{
		this.code = code;
		this.redirectURI = redirectURI;
		this.userId = userId;
		this.clientId = clientId;
	}


	public String getCode()
	{
		return code;
	}


	public String getRedirectURI()
	{
		return redirectURI;
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
