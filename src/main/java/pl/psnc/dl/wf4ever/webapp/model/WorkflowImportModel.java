/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.model;

import java.util.List;

/**
 * @author Piotr Hołubowicz
 *
 */
public class WorkflowImportModel
	extends ImportModel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6654329540413067819L;


	public WorkflowImportModel(ImportType workflowImportType,
			List<MyExpWorkflow> workflows)
	{
		super(workflowImportType, workflows, "Workflows");
	}

}