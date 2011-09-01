/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import pl.psnc.dl.wf4ever.webapp.model.myexp.FileHeader;
import pl.psnc.dl.wf4ever.webapp.model.myexp.PackHeader;
import pl.psnc.dl.wf4ever.webapp.model.myexp.User;
import pl.psnc.dl.wf4ever.webapp.model.myexp.WorkflowHeader;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ImportModel
	implements Serializable
{

	public enum ImportStatus {
		NOT_STARTED, RUNNING, PAUSED, FINISHED
	}

	private static final long serialVersionUID = -6654329540413067819L;

	private List<ResearchObject> researchObjects;

	private List<ResearchObject> researchObjectsProcessed;

	private List<FileHeader> selectedFiles;

	private List<WorkflowHeader> selectedWorkflows;

	private List<PackHeader> selectedPacks;

	private User myExpUser;

	private String message = "Import has not started";

	private List<String> messages = new ArrayList<String>();

	private ImportStatus status = ImportStatus.NOT_STARTED;

	private boolean mergeROs = true;


	public ImportModel(User user)
	{
		super();
		this.myExpUser = user;
		this.researchObjects = new ArrayList<ResearchObject>();
		this.selectedFiles = new ArrayList<FileHeader>();
		this.selectedWorkflows = new ArrayList<WorkflowHeader>();
		this.selectedPacks = new ArrayList<PackHeader>();
	}


	/**
	 * @return the researchObjects
	 */
	public List<ResearchObject> getResearchObjects()
	{
		return researchObjects;
	}


	/**
	 * @return the myExpUser
	 */
	public User getMyExpUser()
	{
		return myExpUser;
	}


	/**
	 * @return the researchObjectsProcessed
	 */
	public List<ResearchObject> getResearchObjectsProcessed()
	{
		return researchObjectsProcessed;
	}


	/**
	 * @param researchObjectsProcessed the researchObjectsProcessed to set
	 */
	public void setResearchObjectsProcessed(
			List<ResearchObject> researchObjectsProcessed)
	{
		this.researchObjectsProcessed = researchObjectsProcessed;
	}


	public void finishProcessingResearchObjects()
	{
		for(ResearchObject ro : researchObjectsProcessed) {
			selectedFiles.addAll(ro.getFiles());
			selectedWorkflows.addAll(ro.getWorkflows());
			selectedPacks.addAll(ro.getPacks());
		}
		this.researchObjects.addAll(researchObjectsProcessed);
		this.researchObjectsProcessed.clear();
	}


	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}


	/**
	 * @param message the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
		if (messages == null)
			messages = new ArrayList<String>();
		messages.add(message);
	}


	/**
	 * @return the messages
	 */
	public String getMessages()
	{
		return StringUtils.join(messages, "\r\n");
	}


	/**
	 * This method doesn't do anything.
	 * @param messages
	 */
	public void setMessages(List<String> messages)
	{
		// do nothing
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


	/**
	 * @return the status
	 */
	public ImportStatus getStatus()
	{
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(ImportStatus status)
	{
		this.status = status;
	}


	public List<FileHeader> getImportedFiles()
	{
		return selectedFiles;
	}


	public List<WorkflowHeader> getImportedWorkflows()
	{
		return selectedWorkflows;
	}


	public List<PackHeader> getImportedPacks()
	{
		return selectedPacks;
	}

}
