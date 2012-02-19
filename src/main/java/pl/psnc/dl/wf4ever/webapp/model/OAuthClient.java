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
@XmlRootElement(name = "client")
public class OAuthClient implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -396954799246175590L;

	public static final String OOB = "OOB";

	private String clientId;

	private String name;

	private String redirectionURI;

	public OAuthClient() {

	}

	/**
	 * @param clientId
	 * @param name
	 * @param redirectionURI
	 */
	public OAuthClient(String clientId, String name, String redirectionURI) {
		this.clientId = clientId;
		this.name = name;
		this.redirectionURI = redirectionURI;
	}

	/**
	 * @return the clientId
	 */
	@XmlElement
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId
	 *            the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the name
	 */
	@XmlElement
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the redirectionURI
	 */
	@XmlElement
	public String getRedirectionURI() {
		return redirectionURI;
	}

	/**
	 * @param redirectionURI
	 *            the redirectionURI to set
	 */
	public void setRedirectionURI(String redirectionURI) {
		this.redirectionURI = redirectionURI;
	}

	public String toString() {
		return getName();
	}
}
