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
public class ImportModel
	implements Serializable
{

	private static final long serialVersionUID = -6654329540413067819L;

	private List<NewResearchObjectModel> researchObjects;

	private List<NewResearchObjectModel> researchObjectsProcessed;

	private MyExpUser myExpUser;

	private String importStatus = "Import has not started";

	private List<String> allImportStatuses = new ArrayList<String>();

	private boolean importActive;

	private boolean mergeROs = true;


	public ImportModel(MyExpUser user)
	{
		super();
		this.myExpUser = user;
		this.researchObjects = new ArrayList<NewResearchObjectModel>();
	}


	/**
	 * @return the researchObjects
	 */
	public List<NewResearchObjectModel> getResearchObjects()
	{
		return researchObjects;
	}


	/**
	 * @return the myExpUser
	 */
	public MyExpUser getMyExpUser()
	{
		return myExpUser;
	}


	/**
	 * @return the researchObjectsProcessed
	 */
	public List<NewResearchObjectModel> getResearchObjectsProcessed()
	{
		return researchObjectsProcessed;
	}


	/**
	 * @param researchObjectsProcessed the researchObjectsProcessed to set
	 */
	public void setResearchObjectsProcessed(
			List<NewResearchObjectModel> researchObjectsProcessed)
	{
		this.researchObjectsProcessed = researchObjectsProcessed;
	}


	public void finishProcessingResearchObjects()
	{
		this.researchObjects.addAll(researchObjectsProcessed);
		this.researchObjectsProcessed.clear();
	}


	/**
	 * @return the importStatus
	 */
	public String getImportStatus()
	{
		return importStatus;
	}


	/**
	 * @param importStatus the importStatus to set
	 */
	public void setImportStatus(String importStatus)
	{
		this.importStatus = importStatus;
		allImportStatuses.add(importStatus);
	}


	/**
	 * @return the importActive
	 */
	public boolean isImportActive()
	{
		return importActive;
	}


	/**
	 * @param importActive the importActive to set
	 */
	public void setImportActive(boolean importActive)
	{
		this.importActive = importActive;
	}


	/**
	 * @return the allImportStatuses
	 */
	public String getAllImportStatuses()
	{
		return StringUtils.join(allImportStatuses, "\r\n");
	}


	/**
	 * @return the mergeROs
	 */
	public boolean isMergeROs()
	{
		return mergeROs;
	}


	/**
	 * @param mergeROs the mergeROs to set
	 */
	public void setMergeROs(boolean mergeROs)
	{
		this.mergeROs = mergeROs;
	}

}
