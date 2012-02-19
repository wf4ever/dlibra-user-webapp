/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Piotr Hołubowicz
 *
 */
@XmlRootElement(name = "user")
public class MyExpUser
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4428655508723987304L;

	private String uri;

	private String resource;

	private int id;

	private String openId;

	private String name;

	private String email;

	private String city;

	private String country;

	private String website;


	public MyExpUser()
	{

	}


	public int getId()
	{
		return id;
	}


	public void setId(int id)
	{
		this.id = id;
	}


	/**
	 * @return the openId
	 */
	@XmlElement(name = "openid-url")
	public String getOpenId()
	{
		return openId;
	}


	/**
	 * @param openId the openId to set
	 */
	public void setOpenId(String openId)
	{
		this.openId = openId;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getEmail()
	{
		return email;
	}


	public void setEmail(String email)
	{
		this.email = email;
	}


	public String getCity()
	{
		return city;
	}


	public void setCity(String city)
	{
		this.city = city;
	}


	public String getCountry()
	{
		return country;
	}


	public void setCountry(String country)
	{
		this.country = country;
	}


	public String getWebsite()
	{
		return website;
	}


	public void setWebsite(String website)
	{
		this.website = website;
	}


	/**
	 * @return the uri
	 */
	@XmlAttribute
	public String getUri()
	{
		return uri;
	}


	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri)
	{
		this.uri = uri;
	}


	/**
	 * @return the resource
	 */
	@XmlAttribute
	public String getResource()
	{
		return resource;
	}


	/**
	 * @param resource the resource to set
	 */
	public void setResource(String resource)
	{
		this.resource = resource;
	}


}
