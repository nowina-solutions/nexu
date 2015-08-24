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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Controller
public class PageController {

	@Value("${baseUrl}")
	private String baseUrl;

	@Value("${nexuUrl}")
	private String nexuUrl = "http://localhost:9876/";

	private Template template;

	public PageController() {
		try {
			Configuration cfg = new Configuration();
			cfg.setClassForTemplateLoading(getClass(), "/");
			this.template = cfg.getTemplate("nexu.js.ftl", "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	@RequestMapping("/")
	public String home() {
		return "index";
	}

	@RequestMapping("/nexu.js")
	public void loadScript(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		resp.setContentType("text/javascript");
		resp.setCharacterEncoding("UTF-8");
		Writer outWriter = new OutputStreamWriter(resp.getOutputStream(), Charset.forName("UTF-8"));

		Map<String, String> model = new HashMap<>();

		model.put("baseUrl", baseUrl);
		model.put("nexuUrl", nexuUrl);

		template.process(model, outWriter);
	}

}
