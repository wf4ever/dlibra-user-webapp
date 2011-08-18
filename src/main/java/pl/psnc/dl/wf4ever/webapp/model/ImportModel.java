/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ImportModel
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6654329540413067819L;

	public enum ImportType {
		ALL_AS_1_RO, ALL_AS_MANY_ROS, NOTHING, CUSTOM
	};

	private ImportType fileImportType;


	public ImportModel(ImportType fileImportType)
	{
		super();
		this.setFileImportType(fileImportType);
	}


	/**
	 * @return the fileImportType
	 */
	public ImportType getFileImportType()
	{
		return fileImportType;
	}


	/**
	 * @param fileImportType the fileImportType to set
	 */
	public void setFileImportType(ImportType fileImportType)
	{
		this.fileImportType = fileImportType;
	}


}
