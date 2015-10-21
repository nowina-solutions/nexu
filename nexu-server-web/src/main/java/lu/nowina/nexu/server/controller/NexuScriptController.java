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
package lu.nowina.nexu.server.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lu.nowina.nexu.ConfigurationException;
import lu.nowina.nexu.TechnicalException;

@Controller
public class NexuScriptController {

	private static final String UTF8 = "UTF-8";

	private static final String TEXT_JAVASCRIPT = "text/javascript";

	private static final String NEXUJS_TEMPLATE = "nexu.ftl.js";

	private static final Logger logger = Logger.getLogger(NexuScriptController.class.getName());

	@Value("${baseUrl}")
	String baseUrl;

	@Value("${nexuUrl}")
	String nexuUrl = "http://localhost:9876/";

	private Template template;

	public NexuScriptController() {
		try {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
			cfg.setClassForTemplateLoading(getClass(), "/");
			this.template = cfg.getTemplate(NEXUJS_TEMPLATE, UTF8);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Cannot find template for nexu", e);
			throw new ConfigurationException("Cannot find template for nexu");
		}
	}

	@PostConstruct
	public void postConstruct() {
		if(baseUrl == null) {
			throw new ConfigurationException("Configuration must define 'baseUrl'");
		}
		if(nexuUrl == null) {
			throw new ConfigurationException("Configuration must define 'nexuUrl'");
		}
	}
	
	@RequestMapping(value = "/nexu.js")
	public ResponseEntity<String> loadScript() {

		StringWriter writer = new StringWriter();

		Map<String, String> model = new HashMap<>();

		model.put("baseUrl", baseUrl);
		model.put("nexuUrl", nexuUrl);

		try {
			template.process(model, writer);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Cannot process template", e);
			throw new TechnicalException("Cannot process template");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, TEXT_JAVASCRIPT);
		headers.add(HttpHeaders.CONTENT_ENCODING, UTF8);

		ResponseEntity<String> entity = new ResponseEntity<>(writer.toString(), headers, HttpStatus.OK);
		return entity;

	}

}
