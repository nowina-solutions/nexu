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
package lu.nowina.nexu.rest;

import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

import eu.europa.esig.dss.ToBeSigned;
import lu.nowina.nexu.api.GetCertificateRequest;
import lu.nowina.nexu.api.GetCertificateResponse;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.Execution;
import lu.nowina.nexu.api.SignatureRequest;
import lu.nowina.nexu.api.SignatureResponse;
import lu.nowina.nexu.api.plugin.HttpPlugin;

/**
 * Default implementation of HttpPlugin for NexU. 
 * 
 * @author David Naramski
 *
 */
public class RestHttpPlugin implements HttpPlugin {

	private static final Logger logger = Logger.getLogger(RestHttpPlugin.class.getName());
	
	private static final Gson gson = new Gson();
	
	@Override
	public void init(String pluginId, NexuAPI api) {
	}
	
	@Override
	public void process(NexuAPI api, HttpServletRequest req, HttpServletResponse resp) throws Exception {

		String uri = req.getRequestURI();
		logger.info("URI " + uri);
		
		String target = req.getPathInfo();
		logger.info("PathInfo " + target);

		String payload = IOUtils.toString(req.getInputStream());
		logger.info("Payload '" + payload + "'");
		
		if ("/sign".equals(target)) {

			logger.info("Signature");
			SignatureRequest r = new SignatureRequest();
			if(StringUtils.isEmpty(payload)) {
				r = new SignatureRequest();
				
				String data = req.getParameter("data");
				if(data != null) {
					logger.info("Data to sign " + data);
					
					ToBeSigned tbs = new ToBeSigned();
					tbs.setBytes(data.getBytes());
					r.setTbs(tbs);
				}
				
			} else {
				r = gson.fromJson(payload, SignatureRequest.class);
			}
			
			Execution<?> respObj = api.sign(r);
			
			PrintWriter writer = resp.getWriter();
			writer.write(gson.toJson(respObj));;
			writer.close();

		} else if ("/certificates".equals(target)) {

			logger.info("API call certificates");
			GetCertificateRequest payloadObj = null;
			if(StringUtils.isEmpty(payload)) {
				payloadObj = new GetCertificateRequest();
			} else {
				payloadObj = gson.fromJson(payload, GetCertificateRequest.class);
			}
			
			logger.info("Call API");
			Execution<?> respObj = api.getCertificate(payloadObj);
			PrintWriter writer = resp.getWriter();
			writer.write(gson.toJson(respObj));;
			writer.close();

		}

	}

}
