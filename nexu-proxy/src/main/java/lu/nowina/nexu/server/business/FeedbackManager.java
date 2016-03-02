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
package lu.nowina.nexu.server.business;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;

import lu.nowina.nexu.ConfigurationException;
import lu.nowina.nexu.TechnicalException;
import lu.nowina.nexu.api.Feedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeedbackManager {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackManager.class.getName());

	private static final Pattern UUID_PATTERN = Pattern.compile("........-....-....-....-............");

	@Value("${feedback.repository}")
	private String repository;

	private JAXBContext ctx;

	private File repositoryDir;

	public FeedbackManager() throws Exception {

		try {
			ctx = JAXBContext.newInstance(Feedback.class);
		} catch (Exception e) {
			logger.error("Cannot instanciante JAXBContext", e);
			throw new TechnicalException("Cannot instanciate JAXBContext for Feedback");
		}
	}

	@PostConstruct
	public void postConstruct() {
		if (repository == null) {
			throw new ConfigurationException("Configuration must defined 'repository'");
		}

		repositoryDir = new File(repository);
		if (!repositoryDir.exists()) {
			if(!repositoryDir.mkdirs()) {
				throw new ConfigurationException("Cannot create repository " + repositoryDir.getAbsolutePath());
			}
		}

		if (!repositoryDir.exists() || !repositoryDir.isDirectory() || !repositoryDir.canWrite()) {
			throw new ConfigurationException(repositoryDir.getAbsolutePath() + " cannot be used for repository");
		}

	}

	public void reportError(Feedback feedback) throws Exception {
		String id = UUID.randomUUID().toString();
		File reportFile = getFile(id);
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(reportFile));
		ctx.createMarshaller().marshal(feedback, out);
		out.close();
	}

	private File getFile(String id) {
		File reportFile = new File(repositoryDir, id);
		return reportFile;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public int countFeedback() {
		return repositoryDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return UUID_PATTERN.matcher(name).matches();
			}
		}).length;
	}
}
