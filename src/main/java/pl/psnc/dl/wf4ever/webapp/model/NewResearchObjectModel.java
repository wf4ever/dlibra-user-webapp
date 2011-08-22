/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Piotr Hołubowicz
 *
 */
public class NewResearchObjectModel
	implements Serializable
{

	private static final long serialVersionUID = -4038193471676170594L;

	private boolean existing;

	private List< ? extends MyExpResource> resources;

	private List<MyExpFile> files = new ArrayList<MyExpFile>();

	private List<MyExpWorkflow> workflows = new ArrayList<MyExpWorkflow>();

	private List<MyExpPack> packs = new ArrayList<MyExpPack>();

	private String name;


	public NewResearchObjectModel()
	{
	}


	public NewResearchObjectModel(MyExpFile file)
	{
		files.add(file);
	}


	public NewResearchObjectModel(MyExpWorkflow workflow)
	{
		workflows.add(workflow);
	}


	public NewResearchObjectModel(MyExpPack pack)
	{
		packs.add(pack);
	}


	/**
	 * @return the existing
	 */
	public boolean isExisting()
	{
		return existing;
	}


	/**
	 * @param existing the existing to set
	 */
	public void setExisting(boolean existing)
	{
		this.existing = existing;
	}


	/**
	 * @return the resources
	 */
	public List< ? extends MyExpResource> getResources()
	{
		return resources;
	}


	/**
	 * @param resources the resources to set
	 */
	public void setResources(List< ? extends MyExpResource> resources)
	{
		this.resources = resources;
	}


	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * @return the files
	 */
	public List<MyExpFile> getFiles()
	{
		return files;
	}


	/**
	 * @param files the files to set
	 */
	public void setFiles(List<MyExpFile> files)
	{
		this.files = files;
	}


	/**
	 * @return the workflows
	 */
	public List<MyExpWorkflow> getWorkflows()
	{
		return workflows;
	}


	/**
	 * @param workflows the workflows to set
	 */
	public void setWorkflows(List<MyExpWorkflow> workflows)
	{
		this.workflows = workflows;
	}


	/**
	 * @return the packs
	 */
	public List<MyExpPack> getPacks()
	{
		return packs;
	}


	/**
	 * @param packs the packs to set
	 */
	public void setPacks(List<MyExpPack> packs)
	{
		this.packs = packs;
	}
}
