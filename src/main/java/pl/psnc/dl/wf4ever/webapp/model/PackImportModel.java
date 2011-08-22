/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.util.List;

/**
 * @author Piotr Hołubowicz
 *
 */
public class PackImportModel
	extends ImportModel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6654329540413067819L;


	public PackImportModel(ImportType packImportType, List<MyExpPack> packs)
	{
		super(packImportType, packs, "Packs");
	}

}
