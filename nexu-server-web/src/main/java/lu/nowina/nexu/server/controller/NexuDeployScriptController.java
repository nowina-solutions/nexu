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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Controller
public class NexuDeployScriptController {

	@Value("${baseUrl}")
	private String baseUrl;

	@Value("${nexuUrl}")
	private String nexuUrl = "http://localhost:9876/";

	private Template template;

	public NexuDeployScriptController() {
		try {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
			cfg.setClassForTemplateLoading(getClass(), "/");
			this.template = cfg.getTemplate("nexu_deploy.ftl.js", "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	@RequestMapping("/js/nexu-deploy.js")
	public ResponseEntity<String> loadScript() throws Exception {

		StringWriter outWriter = new StringWriter();

		Map<String, String> model = new HashMap<>();

		model.put("baseUrl", baseUrl);
		model.put("nexuUrl", nexuUrl);

		template.process(model, outWriter);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("text/javascript"));
		
		return new ResponseEntity<String>(outWriter.toString(), headers, HttpStatus.ACCEPTED);
	}

}
