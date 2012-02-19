/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

	private Date created;

	private Date lastUsed;

	public AccessToken() {

	}

	/**
	 * @return the token
	 */
	@Id
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
	@ManyToOne
	@JoinColumn(nullable = false)
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

	@Basic
	@XmlElement
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the lastUsed
	 */
	@Basic
	@XmlElement
	public Date getLastUsed() {
		return lastUsed;
	}

	/**
	 * @param lastUsed
	 *            the lastUsed to set
	 */
	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

}
