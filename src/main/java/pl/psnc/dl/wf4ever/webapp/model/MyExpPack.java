/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

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
public class MyExpPack
	extends MyExpResource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3146768347985843474L;

	private List<MyExpSimpleResource> resources = new ArrayList<MyExpSimpleResource>();

	private int id;


	/**
	 * @return the resources
	 */
	@XmlElementWrapper(name = "internal-pack-items")
	@XmlElements({ @XmlElement(name = "file", type = MyExpFile.class),
			@XmlElement(name = "workflow", type = MyExpWorkflow.class)})
	public List<MyExpSimpleResource> getResources()
	{
		return resources;
	}


	/**
	 * @param resources the resources to set
	 */
	public void setResources(List<MyExpSimpleResource> resources)
	{
		this.resources = resources;
	}


	@Override
	public String getFullUrl()
	{
		return getUri() + "&elements=internal-pack-items,id";
	}


	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

}
