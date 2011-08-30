/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Piotr Hołubowicz
 *
 */
@XmlRootElement(name = "file")
public class MyExpFile
	extends MyExpSimpleResource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1547898914095065327L;

	private String filename;

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


	@Override
	public String getFullUrl()
	{
		return getUri() + "&elements=filename,content,content-type,id";
	}



}
