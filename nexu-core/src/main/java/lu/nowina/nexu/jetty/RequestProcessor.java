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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.mortbay.jetty.handler.AbstractHandler;

import lu.nowina.nexu.InternalAPI;
import lu.nowina.nexu.UserPreferences;
import lu.nowina.nexu.api.plugin.HttpPlugin;

public class RequestProcessor extends AbstractHandler {

	private static final Logger logger = Logger.getLogger(RequestProcessor.class.getName());

	private UserPreferences config;

	private InternalAPI api;

	public void setConfig(InternalAPI api, UserPreferences config) {
		this.api = api;
		this.config = config;
	}

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
			throws IOException, ServletException {

		if (!"0:0:0:0:0:0:0:1".equals(request.getRemoteHost()) && !"127.0.0.1".equals(request.getRemoteHost())) {
			logger.warning("Cannot accept request from " + request.getRemoteHost());
			response.setContentType("text/html;charset=utf-8");
			response.getWriter().write("Please connect from localhost");
			response.getWriter().close();
			return;
		}

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

		logger.info("Request " + target);

		if ("/favicon.ico".equals(target)) {
			response.setContentType("image/png");
			InputStream in = this.getClass().getResourceAsStream("/tray-icon.png");
			ServletOutputStream out = response.getOutputStream();
			IOUtils.copy(in, out);
			in.close();
			out.close();
		} else if ("/".equals(target) || "/nexu-info".equals(target)) {
			response.setContentType("text/plain");
			ServletOutputStream out = response.getOutputStream();
			out.write("1.0".getBytes());
			out.close();
		} else {

			logger.info("Process request " + target);
			try {
				HttpPlugin httpPlugin = api.getPlugin("rest");
				httpPlugin.process(api, new DelegatedHttpServerRequest(request, "/rest"), response);
			} catch (Exception e) {
			    logger.log(Level.SEVERE, "Cannot process request", e);
				response.setContentType("text/plain;charset=utf-8");
				e.printStackTrace(response.getWriter());
				response.getWriter().close();
				response.setStatus(500);
			}

		}

	}

}