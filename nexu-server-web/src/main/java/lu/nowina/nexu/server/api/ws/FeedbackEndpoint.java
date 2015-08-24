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

import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lu.nowina.nexu.api.Feedback;

/**
 * WebService exposed to NexU install base. 
 * 
 * @author David Naramski
 *
 */
@Service
@WebService(targetNamespace="http://api.nexu.nowina.lu/")
public class FeedbackEndpoint {

	@Value("${repository}")
	private String repository;
	
	private JAXBContext ctx; 
	
	private File repositoryDir;
	
	public FeedbackEndpoint() throws Exception {
		ctx = JAXBContext.newInstance(Feedback.class);
	}

	@PostConstruct
	public void init() {
		repositoryDir = new File(repository);
		
		if(!repositoryDir.exists() || !repositoryDir.isDirectory() || !repositoryDir.canWrite()) {
			throw new IllegalArgumentException(repositoryDir.getAbsolutePath() + " cannot be used for repository");
		}
		
	}
	
	public void reportError(Feedback feedback) throws Exception {
		File reportFile = new File(repositoryDir, UUID.randomUUID().toString());
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(reportFile));
		ctx.createMarshaller().marshal(feedback, out);
		out.close();
	}

}
