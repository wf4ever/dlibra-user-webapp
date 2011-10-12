package pl.psnc.dl.wf4ever.webapp.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "access-tokens")
public class AccessTokenList
{

	protected List<AccessToken> list;


	public AccessTokenList()
	{
	}


	public AccessTokenList(List<AccessToken> list)
	{
		this.list = list;
	}


	@XmlElement(name = "access-token")
	public List<AccessToken> getList()
	{
		return list;
	}
}