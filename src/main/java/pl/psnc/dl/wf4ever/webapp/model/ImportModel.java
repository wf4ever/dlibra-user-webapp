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
public class ImportModel
	implements Serializable
{

	private static final long serialVersionUID = -6654329540413067819L;

	private List<NewResearchObjectModel> researchObjects;

	private MyExpUser myExpUser;


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

}
