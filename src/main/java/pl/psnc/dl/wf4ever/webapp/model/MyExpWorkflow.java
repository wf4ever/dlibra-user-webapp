/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.wicket.util.crypt.Base64;

/**
 * @author Piotr Hołubowicz
 *
 */
@XmlRootElement(name = "workflow")
public class MyExpWorkflow
	extends MyExpResource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3019438116219497825L;

	private String contentUri;

	private String content;

	private String contentType;


	/**
	 * @return the filename
	 */
	public String getFilename()
	{
		return contentUri.substring(contentUri.lastIndexOf('/') + 1);
	}


	/**
	 * @return the content base64-encoded
	 */
	public String getContent()
	{
		return content;
	}


	/**
	 * @param content the content to set, base64-encoded
	 */
	public void setContent(String content)
	{
		this.content = content;
	}


	public String getContentDecoded()
	{
		return new String(Base64.decodeBase64(content));
	}


	/**
	 * @return the contentType
	 */
	@XmlElement(name = "content-type")
	public String getContentType()
	{
		return contentType;
	}


	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}


	/**
	 * @return the contentUri
	 */
	@XmlElement(name = "content-uri")
	public String getContentUri()
	{
		return contentUri;
	}


	/**
	 * @param contentUri the contentUri to set
	 */
	public void setContentUri(String contentUri)
	{
		this.contentUri = contentUri;
	}
}
