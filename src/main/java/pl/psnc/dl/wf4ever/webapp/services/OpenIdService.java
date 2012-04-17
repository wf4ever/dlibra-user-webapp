package pl.psnc.dl.wf4ever.webapp.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters.NamedPair;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegRequest;
import org.openid4java.message.sreg.SRegResponse;

import pl.psnc.dl.wf4ever.webapp.model.OpenIdUser;

/**
 * Most of this code modeled after ConsumerServlet, part of the openid4java sample code
 * available at http://code.google.com/p/openid4java/wiki/SampleConsumer, some code added
 * by J Steven Perry.
 * 
 * @author Piotr Hołubowicz
 */
public class OpenIdService
{

	private static final Logger log = Logger.getLogger(OpenIdService.class);

	public static final Map<String, String> axFields = new HashMap<String, String>();
	static {
		axFields.put("FirstName", "http://axschema.org/namePerson/first");
		axFields.put("LastName", "http://axschema.org/namePerson/last");
		axFields.put("Country", "http://axschema.org/contact/country/home");
		axFields.put("Language", "http://axschema.org/pref/language");
		axFields.put("Email", "http://axschema.org/contact/email");
	}

	public static final Map<String, String> myopenidAxFields = new HashMap<String, String>();
	static {
		myopenidAxFields.put("FullName", "http://schema.openid.net/namePerson");
		myopenidAxFields.put("Country", "http://schema.openid.net/country/home");
		myopenidAxFields.put("Language", "http://schema.openid.net/pref/language");
		myopenidAxFields.put("Email", "http://schema.openid.net/contact/email");
	}

	public static final Set<String> sRegFields = new HashSet<String>();

	static {
		sRegFields.add("email");
		sRegFields.add("fullname");
		sRegFields.add("country");
		sRegFields.add("language");
	}


	/**
	 * Perform discovery on the User-Supplied identifier and return the
	 * DiscoveryInformation object that results from Association with the OP. This will
	 * probably be needed by the caller (stored in Session perhaps?).
	 * 
	 * I'm not thrilled about ConsumerManager being static, but it is very important to
	 * openid4java that the ConsumerManager object be the same instance all through a
	 * conversation (discovery, auth request, auth response) with the OP. I didn't dig
	 * terribly deeply, but suspect that part of the key exchange or the nonce uses the
	 * ConsumerManager's hash, or some other instance-specific construct to do its thing.
	 * 
	 * @param userSuppliedIdentifier
	 *            The User-Supplied identifier. It may already be normalized.
	 * 
	 * @return DiscoveryInformation - The resulting DisoveryInformation object returned by
	 *         openid4java following successful association with the OP.
	 */
	public static DiscoveryInformation performDiscoveryOnUserSuppliedIdentifier(String userSuppliedIdentifier)
	{
		ConsumerManager consumerManager = getConsumerManager();
		try {
			// Perform discover on the User-Supplied Identifier
			@SuppressWarnings("unchecked")
			List<DiscoveryInformation> discoveries = consumerManager.discover(userSuppliedIdentifier);
			// Pass the discoveries to the associate() method...
			return consumerManager.associate(discoveries);

		}
		catch (DiscoveryException e) {
			String message = "Error occurred during discovery!";
			log.error(message, e);
			throw new RuntimeException(message, e);
		}
	}


	/**
	 * Create an OpenID Auth Request, using the DiscoveryInformation object return by the
	 * openid4java library.
	 * 
	 * This method also uses the Exchange Attribute Extension to grant the Relying Party
	 * (RP).
	 * 
	 * @param discoveryInformation
	 *            The DiscoveryInformation that should have been previously obtained from
	 *            a call to performDiscoveryOnUserSuppliedIdentifier().
	 * 
	 * @param returnToUrl
	 *            The URL to which the OP will redirect once the authentication call is
	 *            complete.
	 * 
	 * @return AuthRequest - A "good-to-go" AuthRequest object packed with all kinds of
	 *         great OpenID goodies for the OpenID Provider (OP). The caller must take
	 *         this object and forward it on to the OP. Or call processAuthRequest() -
	 *         part of this Service Class.
	 */
	public static AuthRequest createOpenIdAuthRequest(DiscoveryInformation discoveryInformation, String returnToUrl)
	{
		AuthRequest ret = null;
		//
		try {
			// Create the AuthRequest object
			ret = getConsumerManager().authenticate(discoveryInformation, returnToUrl);
			ret.addExtension(createSRegRequest());
			boolean myOpenID = discoveryInformation.getOPEndpoint().getHost().equals("www.myopenid.com");
			FetchRequest axRequest = myOpenID ? createAttributeExchangeRequestMyOpenID()
					: createAttributeExchangeRequest();
			ret.addExtension(axRequest);

		}
		catch (Exception e) {
			String message = "Exception occurred while building AuthRequest object!";
			log.error(message, e);
			throw new RuntimeException(message, e);
		}
		return ret;
	}


	public static FetchRequest createAttributeExchangeRequest()
	{
		FetchRequest fetch = FetchRequest.createFetchRequest();
		try {
			for (Map.Entry<String, String> e : axFields.entrySet()) {
				fetch.addAttribute(e.getKey(), e.getValue(), true);
			}
		}
		catch (MessageException e) {
			log.warn("Erorr when adding attributes", e);
		}
		return fetch;
	}


	public static FetchRequest createAttributeExchangeRequestMyOpenID()
	{
		FetchRequest fetch = FetchRequest.createFetchRequest();
		try {
			for (Map.Entry<String, String> e : myopenidAxFields.entrySet()) {
				fetch.addAttribute(e.getKey(), e.getValue(), false);
			}
		}
		catch (MessageException e) {
			log.warn("Erorr when adding attributes", e);
		}
		return fetch;
	}


	public static SRegRequest createSRegRequest()
	{
		SRegRequest fetch = SRegRequest.createFetchRequest();
		for (String e : sRegFields) {
			fetch.addAttribute(e, false);
		}
		return fetch;
	}


	/**
	 * Processes the returned information from an authentication request from the OP.
	 */
	public static OpenIdUser processReturn(DiscoveryInformation discoveryInformation, PageParameters pageParameters,
			String returnToUrl)
	{
		try {
			// Verify the Information returned from the OP
			// / This is required according to the spec
			ParameterList response = new ParameterList(convertToMap(pageParameters));
			VerificationResult verificationResult = getConsumerManager().verify(returnToUrl, response,
				discoveryInformation);
			Identifier verifiedIdentifier = verificationResult.getVerifiedId();
			if (verifiedIdentifier != null) {
				AuthSuccess authSuccess = (AuthSuccess) verificationResult.getAuthResponse();
				if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
					MessageExtension extension = authSuccess.getExtension(AxMessage.OPENID_NS_AX);
					if (extension instanceof FetchResponse) {
						return provisionRegistrationModel(verifiedIdentifier, (FetchResponse) extension);
					}
				}
				else {
					log.warn("Authentication response does not have a axMessage extension");
				}
				if (authSuccess.hasExtension(SRegMessage.OPENID_NS_SREG)) {
					MessageExtension extension = authSuccess.getExtension(SRegMessage.OPENID_NS_SREG);
					if (extension instanceof SRegResponse) {
						return provisionRegistrationModel(verifiedIdentifier, (SRegResponse) extension);
					}
				}
				else {
					log.warn("Authentication response does not have a SRegMessage extension");
				}
				log.warn("No extensions supported, provisioning only the OpenID");
				return provisionRegistrationModel(verifiedIdentifier);
			}
			else {
				log.warn("Verified identifier is null");
			}
		}
		catch (Exception e) {
			String message = "Exception occurred while verifying response!";
			log.error(message, e);
			throw new RuntimeException(message, e);
		}
		return null;
	}


	private static OpenIdUser provisionRegistrationModel(Identifier verifiedIdentifier)
	{
		OpenIdUser user = new OpenIdUser();
		user.setOpenId(verifiedIdentifier.getIdentifier());
		return user;
	}


	private static OpenIdUser provisionRegistrationModel(Identifier verifiedIdentifier, FetchResponse axResponse)
	{
		OpenIdUser user = new OpenIdUser();
		user.setOpenId(verifiedIdentifier.getIdentifier());
		String value;
		value = axResponse.getAttributeValue("Email");
		if (value != null && user.getEmailAddress() == null) {
			user.setEmailAddress(value);
		}
		value = axResponse.getAttributeValue("FirstName");
		if (value != null && user.getFirstName() == null) {
			user.setFirstName(value);
		}
		value = axResponse.getAttributeValue("LastName");
		if (value != null && user.getLastName() == null) {
			user.setLastName(value);
		}
		value = axResponse.getAttributeValue("FullName");
		if (value != null && user.getFullName() == null) {
			user.setFullName(value);
		}
		value = axResponse.getAttributeValue("Language");
		if (value != null && user.getLanguage() == null) {
			user.setLanguage(value);
		}
		value = axResponse.getAttributeValue("Country");
		if (value != null && user.getCountry() == null) {
			user.setCountry(value);
		}
		if (user.getFullName() == null) {
			if (user.getFirstName() != null && user.getLastName() != null) {
				user.setFullName(user.getFirstName() + " " + user.getLastName());
			}
			else {
				if (user.getFirstName() != null) {
					user.setFullName(user.getFirstName());
				}
				else {
					user.setFullName(user.getLastName());
				}
			}
		}
		return user;
	}


	private static OpenIdUser provisionRegistrationModel(Identifier verifiedIdentifier, SRegResponse res)
	{
		OpenIdUser user = new OpenIdUser();
		user.setOpenId(verifiedIdentifier.getIdentifier());
		String value;
		value = res.getAttributeValue("email");
		if (value != null && user.getEmailAddress() == null) {
			user.setEmailAddress(value);
		}
		value = res.getAttributeValue("fullname");
		if (value != null && user.getFullName() == null) {
			user.setFullName(value);
		}
		value = res.getAttributeValue("language");
		if (value != null && user.getLanguage() == null) {
			user.setLanguage(value);
		}
		value = res.getAttributeValue("country");
		if (value != null && user.getCountry() == null) {
			user.setCountry(value);
		}
		return user;
	}


	private static Map<String, String> convertToMap(PageParameters pageParameters)
	{
		Map<String, String> res = new HashMap<String, String>();
		for (NamedPair np : pageParameters.getAllNamed()) {
			res.put(np.getKey(), np.getValue());
		}
		return res;
	}

	private static ConsumerManager consumerManager;


	/**
	 * Retrieves an instance of the ConsumerManager object. It is static (see note in
	 * Class-level JavaDoc comments above) because openid4java likes it that way.
	 * 
	 * Note: if you are planning to debug the code, set the lifespan parameter of the
	 * InMemoryNonceVerifier high enough to outlive your debug cycle, or you may notice
	 * Nonce Verification errors. Depending on where you are debugging, this might pose an
	 * artificial problem for you (of your own making) that has nothing to do with either
	 * your code or openid4java.
	 * 
	 * @return ConsumerManager - The ConsumerManager object that handles communication
	 *         with the openid4java API.
	 */
	private static ConsumerManager getConsumerManager()
	{
		if (consumerManager == null) {
			consumerManager = new ConsumerManager();
			consumerManager.setAssociations(new InMemoryConsumerAssociationStore());
			consumerManager.setNonceVerifier(new InMemoryNonceVerifier(10000));
		}
		return consumerManager;
	}

}
