/**
 * © Nowina Solutions, 2015-2015
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu.server.api.ws;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.server.ConfigurationException;
import lu.nowina.nexu.server.TechnicalException;

/**
 * WebService exposed to NexU install base. 
 * 
 * @author David Naramski
 *
 */
@Service
@WebService(targetNamespace="http://api.nexu.nowina.lu/")
public class FeedbackEndpoint {

	private static final Logger logger = Logger.getLogger(FeedbackEndpoint.class.getName());
	
	@Value("${repository}")
	private String repository;
	
	private JAXBContext ctx; 
	
	private File repositoryDir;
	
	public FeedbackEndpoint() throws Exception {
		
		try {
		ctx = JAXBContext.newInstance(Feedback.class);
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Cannot instanciante JAXBContext", e);
			throw new TechnicalException("Cannot instanciate JAXBContext for Feedback");
		}
	}

	@PostConstruct
	public void postConstruct() {
		if(repository == null) {
			throw new ConfigurationException("Configuration must defined 'repository'");
		}

		repositoryDir = new File(repository);
		
		if(!repositoryDir.exists() || !repositoryDir.isDirectory() || !repositoryDir.canWrite()) {
			throw new ConfigurationException(repositoryDir.getAbsolutePath() + " cannot be used for repository");
		}
		
	}
	
	@WebMethod
	public void reportError(Feedback feedback) throws Exception {
		File reportFile = new File(repositoryDir, UUID.randomUUID().toString());
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(reportFile));
		ctx.createMarshaller().marshal(feedback, out);
		out.close();
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}
	
}
