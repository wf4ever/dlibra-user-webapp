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
@XmlRootElement(name = "file")
public class MyExpFile
	extends MyExpResource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1547898914095065327L;

	private String filename;

	private String content;

	private String contentType;


	/**
	 * @return the filename
	 */
	public String getFilename()
	{
		return filename;
	}


	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
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

}
