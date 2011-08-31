/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model.myexp;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Piotr Hołubowicz
 *
 */
@XmlRootElement(name = "file")
public class FileHeader
	extends ResourceHeader
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1547898914095065327L;


	@Override
	public String getResourceUrl()
	{
		return getUri() + "&elements=filename,content,content-type,id,title";
	}

}
