/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model.myexp;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Piotr Hołubowicz
 *
 */
@XmlRootElement(name = "workflow")
public class WorkflowHeader
	extends ResourceHeader
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3019438116219497825L;


	@Override
	public String getResourceUrl()
	{
		return getUri() + "&elements=content,content-uri,content-type,id,title";
	}
}
