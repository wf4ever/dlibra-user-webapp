/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Piotr Hołubowicz
 *
 */
public abstract class ImportModel
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6654329540413067819L;

	public enum ImportType {
		ALL_AS_1_RO, ALL_AS_MANY_ROS, NOTHING, CUSTOM
	};

	protected ImportType importType;

	protected boolean resourceListVisible;

	protected List< ? extends MyExpResource> resources;

	protected String resourceName;


	public ImportModel(ImportType fileImportType,
			List< ? extends MyExpResource> resources, String resourceName)
	{
		super();
		this.setImportType(fileImportType);
		this.setResourceListVisible(false);
		this.resources = resources;
		this.resourceName = resourceName;
	}


	/**
	 * @return the fileImportType
	 */
	public ImportType getImportType()
	{
		return importType;
	}


	/**
	 * @param importType the fileImportType to set
	 */
	public void setImportType(ImportType importType)
	{
		this.importType = importType;
	}


	/**
	 * @return the resourceListVisible
	 */
	public boolean isResourceListVisible()
	{
		return resourceListVisible;
	}


	/**
	 * @param resourceListVisible the resourceListVisible to set
	 */
	public void setResourceListVisible(boolean resourceListVisible)
	{
		this.resourceListVisible = resourceListVisible;
	}


	public String getResourceListDisplayStyle()
	{
		if (isResourceListVisible()) {
			return "display:block";
		}
		else {
			return "display:none";
		}
	}


	public String getHideShowResourceListLabel()
	{
		if (isResourceListVisible()) {
			return "Hide " + resourceName.toLowerCase();
		}
		else {
			return "Show " + resourceName.toLowerCase();
		}
	}


	/**
	 * @return the resources
	 */
	public List< ? extends MyExpResource> getResources()
	{
		return resources;
	}


	public String getResourceName()
	{
		return resourceName;
	}

}
