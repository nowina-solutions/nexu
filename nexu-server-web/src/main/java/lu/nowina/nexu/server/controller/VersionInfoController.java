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

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lu.nowina.nexu.generic.NexuInfo;
import lu.nowina.nexu.server.manager.SCDatabaseManager;

@Controller
public class VersionInfoController {

	@Value("${nexuVersion}")
	private String nexuVersion;

	@Autowired
	private SCDatabaseManager databaseManager;
	
	private JAXBContext ctx;

	private Marshaller marshaller;
	
	public VersionInfoController() {
		try {
			ctx = JAXBContext.newInstance(NexuInfo.class);
			marshaller = ctx.createMarshaller();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	@RequestMapping("/info")
	public void info(HttpServletRequest req, HttpServletResponse resp) throws Exception {

		NexuInfo info = new NexuInfo();
		info.setNexuVersion(nexuVersion);
		info.setDatabaseVersion(databaseManager.getDatabaseDigest());
		marshaller.marshal(info, resp.getOutputStream());
		
	}

}
