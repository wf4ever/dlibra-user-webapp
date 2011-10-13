package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clients")
public class OAuthClientList
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1476500812402240650L;

	protected List<OAuthClient> list = new ArrayList<OAuthClient>();


	public OAuthClientList()
	{
	}


	public OAuthClientList(List<OAuthClient> list)
	{
		this.list = list;
	}


	@XmlElement(name = "client")
	public List<OAuthClient> getList()
	{
		return list;
	}
}