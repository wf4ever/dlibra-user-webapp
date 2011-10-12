/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "access-token")
public class AccessToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8724845005623981779L;

	private String token;

	private OAuthClient client;

	public AccessToken() {

	}

	public AccessToken(String token, OAuthClient client) {
		super();
		this.token = token;
		this.client = client;
	}

	/**
	 * @return the token
	 */
	@XmlElement
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the client
	 */
	@XmlElement
	public OAuthClient getClient() {
		return client;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setClient(OAuthClient client) {
		this.client = client;
	}

}
