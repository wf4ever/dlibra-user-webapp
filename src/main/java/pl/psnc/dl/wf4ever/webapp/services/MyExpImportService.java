/**
 * 
 */
package pl.psnc.dl.wf4ever.webapp.services;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.webapp.model.DlibraUser;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel;
import pl.psnc.dl.wf4ever.webapp.model.ImportModel.ImportStatus;
import pl.psnc.dl.wf4ever.webapp.model.myexp.File;
import pl.psnc.dl.wf4ever.webapp.model.myexp.FileHeader;
import pl.psnc.dl.wf4ever.webapp.model.myexp.Pack;
import pl.psnc.dl.wf4ever.webapp.model.myexp.PackHeader;
import pl.psnc.dl.wf4ever.webapp.model.myexp.Resource;
import pl.psnc.dl.wf4ever.webapp.model.myexp.ResourceHeader;
import pl.psnc.dl.wf4ever.webapp.model.myexp.SimpleResource;
import pl.psnc.dl.wf4ever.webapp.model.myexp.Workflow;
import pl.psnc.dl.wf4ever.webapp.model.myexp.WorkflowHeader;
import pl.psnc.dl.wf4ever.webapp.model.ResearchObject;

/**
 * @author Piotr Hołubowicz
 *
 */
public class MyExpImportService
{

	private static final Logger log = Logger
			.getLogger(MyExpImportService.class);

	private static final OAuthService service = MyExpApi.getOAuthService();


	public static void startImport(ImportModel model, Token myExpAccessToken,
			DlibraUser dLibraUser)
	{
		model.setStatus(ImportStatus.RUNNING);
		for (ResearchObject ro : model.getResearchObjects()) {
			try {
				importRO(model, ro, myExpAccessToken, dLibraUser);
			}
			catch (Exception e) {
				log.error("Error during import", e);
				model.setMessage(e.getMessage());
			}
		}
		model.setMessage("Finished");
		model.setStatus(ImportStatus.FINISHED);
	}


	/**
	 * @param model
	 * @param dLibraUser
	 * @param ro
	 * @throws Exception 
	 */
	private static void importRO(ImportModel model, ResearchObject ro,
			Token token, DlibraUser dLibraUser)
		throws Exception
	{
		createRO(model, ro, dLibraUser);
		importFiles(model, ro, token, dLibraUser);
		importWorkflows(model, ro, token, dLibraUser);
		importPacks(model, ro, token, dLibraUser);
	}


	/**
	 * @param model
	 * @param ro
	 * @param token
	 * @param dLibraUser
	 * @throws JAXBException
	 * @throws Exception
	 */
	private static void importFiles(ImportModel model, ResearchObject ro,
			Token token, DlibraUser user)
		throws JAXBException, Exception
	{
		for (FileHeader file : ro.getFiles()) {
			importSimpleResource(model, file, ro.getName(), token, user,
				File.class);
		}
	}


	/**
	 * @param model
	 * @param ro
	 * @param token
	 * @param dLibraUser
	 * @throws JAXBException
	 * @throws Exception
	 */
	private static void importWorkflows(ImportModel model, ResearchObject ro,
			Token token, DlibraUser user)
		throws JAXBException, Exception
	{
		for (WorkflowHeader workflow : ro.getWorkflows()) {
			importSimpleResource(model, workflow, ro.getName(), token, user,
				Workflow.class);
		}
	}


	/**
	 * @param model
	 * @param ro
	 * @param token
	 * @param dLibraUser
	 * @throws JAXBException
	 * @throws Exception
	 */
	private static void importPacks(ImportModel model, ResearchObject ro,
			Token token, DlibraUser user)
		throws JAXBException, Exception
	{
		for (PackHeader pack : ro.getPacks()) {
			OAuthRequest request = new OAuthRequest(Verb.GET,
					pack.getResourceUrl());
			service.signRequest(token, request);
			Response response = request.send();
			Pack p = (Pack) createMyExpResource(response.getBody(),
				Pack.class);
			model.setMessage(String.format("Importing pack \"%d\"", p.getId()));

			for (SimpleResource r : p.getResources()) {
				//				importSimpleResource(model, r, ro.getName(), token, user,
				//					p.getId() + "/");
			}
		}
	}


	private static void importSimpleResource(ImportModel model,
			ResourceHeader res, String roName, Token token,
			DlibraUser user, Class< ? extends SimpleResource> resourceClass)
		throws Exception
	{
		importSimpleResource(model, res, roName, token, user, "", resourceClass);
	}


	private static void importSimpleResource(ImportModel model,
			ResourceHeader res, String roName, Token token,
			DlibraUser user, String path,
			Class< ? extends SimpleResource> resourceClass)
		throws Exception
	{
		OAuthRequest request = new OAuthRequest(Verb.GET, res.getResourceUrl());
		service.signRequest(token, request);
		Response response = request.send();
		SimpleResource r = (SimpleResource) createMyExpResource(
			response.getBody(), resourceClass);
		model.setMessage(String.format("Importing \"%s\"", r.getFilename()));

		DlibraService.sendResource(path + r.getFilename(), roName,
			r.getContentDecoded(), r.getContentType(), user);

	}


	/**
	 * @param model
	 * @param ro
	 * @param dLibraUser
	 * @throws Exception
	 */
	private static void createRO(ImportModel model, ResearchObject ro,
			DlibraUser dLibraUser)
		throws Exception
	{
		model.setMessage(String.format("Creating a Research Object \"%s\"",
			ro.getName()));
		if (!DlibraService.createResearchObjectAndVersion(ro.getName(),
			dLibraUser, model.isMergeROs())) {
			model.setMessage("Merged with an existing Research Object");
		}
	}


	private static Object createMyExpResource(String xml,
			Class< ? extends Resource> resourceClass)
		throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(resourceClass);
		Unmarshaller u = jc.createUnmarshaller();
		StringBuffer xmlStr = new StringBuffer(xml);
		return u.unmarshal(new StreamSource(new StringReader(xmlStr.toString())));
	}

}
