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

	public enum ImportStatus {
		NOT_STARTED, RUNNING, PAUSED, FINISHED
	}

	private static final long serialVersionUID = -6654329540413067819L;

	private List<NewResearchObjectModel> researchObjects;

	private List<NewResearchObjectModel> researchObjectsProcessed;

	private MyExpUser myExpUser;

	private String message = "Import has not started";

	private List<String> messages = new ArrayList<String>();

	private ImportStatus status = ImportStatus.NOT_STARTED;
	
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

}
