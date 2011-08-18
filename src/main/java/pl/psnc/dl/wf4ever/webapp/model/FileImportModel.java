/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.io.Serializable;

/**
 * @author Piotr Hołubowicz
 *
 */
public class FileImportModel
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6654329540413067819L;

	public enum ImportType {
		ALL_AS_1_RO, ALL_AS_MANY_ROS, NOTHING, CUSTOM
	};

	private ImportType importType;


	public FileImportModel(ImportType importType)
	{
		super();
		this.importType = importType;
	}


	/**
	 * @return the importType
	 */
	public ImportType getImportType()
	{
		return importType;
	}


	/**
	 * @param importType the importType to set
	 */
	public void setImportType(ImportType importType)
	{
		this.importType = importType;
	}
}
