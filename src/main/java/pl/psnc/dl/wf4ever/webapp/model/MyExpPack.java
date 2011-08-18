/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Piotr Hołubowicz
 *
 */
public class MyExpPack
{

	private String uri;

	private String resource;
	
	private String title;

	public MyExpPack()
	{

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

	/**
	 * @return the title
	 */
	@XmlValue
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}
}
