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
public class ResourceSelectionModel
	implements Serializable
{

	private static final long serialVersionUID = 5913502930927105275L;

	public enum ImportType {
		ALL_AS_ONE, EACH_SEPARATELY
	}

	private ImportType importType = ImportType.ALL_AS_ONE;

	private List<MyExpFile> selectedFiles;

	private List<MyExpWorkflow> selectedWorkflows;

	private List<MyExpPack> selectedPacks;


	public ResourceSelectionModel()
	{
		this.selectedFiles = new ArrayList<MyExpFile>();
		this.selectedWorkflows = new ArrayList<MyExpWorkflow>();
		this.selectedPacks = new ArrayList<MyExpPack>();
	}


	public List<MyExpFile> getSelectedFiles()
	{
		return selectedFiles;
	}


	public List<MyExpWorkflow> getSelectedWorkflows()
	{
		return selectedWorkflows;
	}


	public List<MyExpPack> getSelectedPacks()
	{
		return selectedPacks;
	}


	public List<ResearchObject> createResearchObjects()
	{
		List<ResearchObject> ros = new ArrayList<ResearchObject>();
		if (importType == ImportType.ALL_AS_ONE) {
			ResearchObject ro = new ResearchObject();
			ro.setFiles(selectedFiles);
			ro.setWorkflows(selectedWorkflows);
			ro.setPacks(selectedPacks);
			ros.add(ro);
		}
		else {
			for (MyExpFile file : selectedFiles) {
				ros.add(new ResearchObject(file));
			}
			for (MyExpWorkflow workflow : selectedWorkflows) {
				ros.add(new ResearchObject(workflow));
			}
			for (MyExpPack pack : selectedPacks) {
				ros.add(new ResearchObject(pack));
			}
		}
		return ros;
	}


	public boolean isValid()
	{
		return !selectedFiles.isEmpty() || !selectedWorkflows.isEmpty()
				|| !selectedPacks.isEmpty();
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
