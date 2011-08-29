/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ResearchObject
	implements Serializable
{

	public static final int NAME_MAX_LEN = 30;

	public static final int CONTENT_DESC_MAX_LEN = 100;

	private static final long serialVersionUID = -4038193471676170594L;

	private boolean existing;

	private List< ? extends MyExpResource> resources;

	private List<MyExpFile> files = new ArrayList<MyExpFile>();

	private List<MyExpWorkflow> workflows = new ArrayList<MyExpWorkflow>();

	private List<MyExpPack> packs = new ArrayList<MyExpPack>();

	private String name;


	public ResearchObject()
	{
	}


	public ResearchObject(MyExpFile file)
	{
		files.add(file);
	}


	public ResearchObject(MyExpWorkflow workflow)
	{
		workflows.add(workflow);
	}


	public ResearchObject(MyExpPack pack)
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


	public MyExpResource getFirstResource()
	{
		if (!packs.isEmpty()) {
			return packs.get(0);
		}
		else if (!workflows.isEmpty()) {
			return workflows.get(0);
		}
		else if (!files.isEmpty()) {
			return files.get(0);
		}
		return null;
	}


	public int getResourcesCount()
	{
		return files.size() + workflows.size() + packs.size();
	}


	public void setDefaultName()
	{
		MyExpResource resource = getFirstResource();
		if (resource == null) {
			name = "";
		}
		else {
			name = StringUtils.left(resource.getTitle(), NAME_MAX_LEN)
					.replace(' ', '_').replace('/', '_');
			if (name.indexOf('_') > -1) {
				name = name.substring(0, name.lastIndexOf('_'));
			}
		}
	}


	public String getContentDesc()
	{
		if (getResourcesCount() > 1) {
			List<String> list = new ArrayList<String>();
			if (!packs.isEmpty())
				list.add("" + packs.size() + " packs");
			if (!workflows.isEmpty())
				list.add("" + workflows.size() + " workflows");
			if (!files.isEmpty())
				list.add("" + files.size() + " files");
			return StringUtils.join(list, ", ");
		}
		else {
			if (!packs.isEmpty())
				return StringUtils.abbreviate("Pack: " + packs.get(0).getTitle(),
					CONTENT_DESC_MAX_LEN);
			if (!workflows.isEmpty())
				return StringUtils.abbreviate("Workflow: " + workflows.get(0).getTitle(), 
					CONTENT_DESC_MAX_LEN);
			if (!files.isEmpty())
				return StringUtils.abbreviate("File: " + files.get(0).getTitle(),
					CONTENT_DESC_MAX_LEN);
		}
		return null;
	}

}
