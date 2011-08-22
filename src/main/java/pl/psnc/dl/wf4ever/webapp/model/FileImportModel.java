/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.util.List;

/**
 * @author Piotr Hołubowicz
 *
 */
public class FileImportModel
	extends ImportModel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6654329540413067819L;


	public FileImportModel(ImportType fileImportType, List<MyExpFile> files)
	{
		super(fileImportType, files, "Files");
	}

}
