/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model.myexp;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Piotr Hołubowicz
 *
 */
@XmlRootElement(name = "pack")
public class Pack
	extends Resource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3146768347985843474L;

	private List<SimpleResource> resources = new ArrayList<SimpleResource>();


	/**
	 * @return the resources
	 */
	@XmlElementWrapper(name = "internal-pack-items")
	@XmlElements({ @XmlElement(name = "file", type = File.class),
			@XmlElement(name = "workflow", type = Workflow.class)})
	public List<SimpleResource> getResources()
	{
		return resources;
	}


	/**
	 * @param resources the resources to set
	 */
	public void setResources(List<SimpleResource> resources)
	{
		this.resources = resources;
	}

}
