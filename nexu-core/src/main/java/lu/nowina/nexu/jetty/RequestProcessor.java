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
package lu.nowina.nexu.jetty;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lu.nowina.nexu.ConfigurationException;
import lu.nowina.nexu.InternalAPI;
import lu.nowina.nexu.TechnicalException;
import lu.nowina.nexu.UserPreferences;
import lu.nowina.nexu.api.plugin.HttpPlugin;
import lu.nowina.nexu.api.plugin.HttpResponse;
import lu.nowina.nexu.api.plugin.HttpStatus;

public class RequestProcessor extends AbstractHandler {

	private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class.getName());

	private static final String UTF8 = "UTF-8";

	private static final String TEXT_JAVASCRIPT = "text/javascript";

	private static final String NEXUJS_TEMPLATE = "nexu.ftl.js";

	private UserPreferences config;

	private InternalAPI api;

	String baseUrl;

	String nexuUrl = "http://localhost:9876/";

	private Template template;

	public RequestProcessor(String baseUrl, String nexuUrl) {
		this.baseUrl = baseUrl;
		this.nexuUrl = nexuUrl;
		try {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
			cfg.setClassForTemplateLoading(getClass(), "/");
			this.template = cfg.getTemplate(NEXUJS_TEMPLATE, UTF8);
		} catch (IOException e) {
			logger.error("Cannot find template for nexu", e);
			throw new ConfigurationException("Cannot find template for nexu");
		}
	}

	public void setConfig(InternalAPI api, UserPreferences config) {
		this.api = api;
		this.config = config;
	}

	@Override
	public void handle(String target, Request arg1, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		if (!"0:0:0:0:0:0:0:1".equals(request.getRemoteHost()) && !"127.0.0.1".equals(request.getRemoteHost())) {
			logger.warn("Cannot accept request from " + request.getRemoteHost());
			response.setContentType("text/html;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.write("Please connect from localhost");
			writer.close();
			return;
		}

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");

		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(200);
			response.getWriter().close();
			return;
		}

		logger.info("Request " + target);

		if ("/favicon.ico".equals(target)) {
			favIcon(response);
		} else if ("/nexu.js".equals(target)) {
			nexuJs(response);
		} else if ("/".equals(target) || "/nexu-info".equals(target)) {
			nexuInfo(response);
		} else {
			httpPlugin(target, request, response);
		}

	}

	private void httpPlugin(String target, HttpServletRequest request, HttpServletResponse response) {
		int index = target.indexOf("/", 1);
		String pluginId = target.substring(target.charAt(0) == '/' ? 1 : 0, index);

		logger.info("Process request " + target + " pluginId: " + pluginId);
		try {
			PrintWriter writer = response.getWriter();
			HttpPlugin httpPlugin = api.getPlugin(pluginId);

			HttpResponse resp = httpPlugin.process(api, new DelegatedHttpServerRequest(request, '/' + pluginId));
			if (resp == null || resp.getContent() == null) {
				throw new TechnicalException("Plugin responded null");
			} else {
				response.setContentType(resp.getContentType());
				writer.write(resp.getContent());
				writer.close();
				if (resp.getHttpStatus() != HttpStatus.OK) {
					response.sendError(resp.getHttpStatus().getHttpCode());
				}
			}

		} catch (Exception e) {
			logger.error("Cannot process request", e);
			try {
				response.sendError(500);
				response.setContentType("text/plain;charset=utf-8");
				PrintWriter writer = response.getWriter();
				e.printStackTrace(writer);
				writer.close();
			} catch (IOException e2) {
				logger.error("Cannot write error !?", e2);
			}
		}
	}

	private void nexuInfo(HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		PrintWriter writer = response.getWriter();
		writer.write("{ \"version\": \"1.0\"}");
		writer.close();
	}

	private void favIcon(HttpServletResponse response) throws IOException {
		response.setContentType("image/png");
		InputStream in = this.getClass().getResourceAsStream("/tray-icon.png");
		ServletOutputStream out = response.getOutputStream();
		IOUtils.copy(in, out);
		in.close();
		out.close();
	}

	private void nexuJs(HttpServletResponse response) throws IOException {

		StringWriter writer = new StringWriter();

		Map<String, String> model = new HashMap<>();

		model.put("baseUrl", baseUrl);
		model.put("nexuUrl", nexuUrl);

		try {
			template.process(model, writer);
		} catch (Exception e) {
			logger.error("Cannot process template", e);
			throw new TechnicalException("Cannot process template");
		}

		response.setContentType(TEXT_JAVASCRIPT);
		PrintWriter out = response.getWriter();
		out.println(writer.toString());
		out.close();

	}

}