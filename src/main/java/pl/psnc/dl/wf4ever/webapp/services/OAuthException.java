/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import org.mortbay.jetty.HttpStatus;
import org.scribe.model.Response;

/**
 * @author Piotr Hołubowicz
 *
 */
public class OAuthException
	extends Exception
{

	private static final long serialVersionUID = 4341885401436426808L;

	private Response response;


	public OAuthException(Response response, String explanation)
	{
		super(String.format("%s (%s)",
			HttpStatus.getResponseLine(response.getCode()), explanation));
		this.response = response;
	}


	public OAuthException(Response response)
	{
		super(HttpStatus.getResponseLine(response.getCode()).toString());
		this.response = response;
	}


	/**
	 * @return the response
	 */
	public Response getResponse()
	{
		return response;
	}

}
