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
package lu.nowina.nexu.generic;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.plugin.HttpStatus;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpDataLoader {

	private static final Logger logger = LoggerFactory.getLogger(HttpDataLoader.class.getName());
	
	private HttpClient client = new HttpClient();

	public byte[] fetchDatabase(String databaseUrl) throws IOException {

		GetMethod get = new GetMethod(databaseUrl);

		EnvironmentInfo info = EnvironmentInfo.buildFromSystemProperties(System.getProperties());

		get.setQueryString(new NameValuePair[] { new NameValuePair("os.name", info.getOsName()), new NameValuePair("os.arch", info.getOsArch()),
				new NameValuePair("os.version", info.getOsVersion()) });

		client.executeMethod(get);

		if(!HttpStatus.OK.equals(get.getStatusCode())) {
			logger.info("Cannot retrieve database from " + databaseUrl + ", status code = " + get.getStatusCode());
			return null;
		}
		
		return get.getResponseBody();

	}

	public byte[] fetchNexuInfo(String infoUrl) throws IOException, JAXBException {

		GetMethod get = new GetMethod(infoUrl);

		EnvironmentInfo info = EnvironmentInfo.buildFromSystemProperties(System.getProperties());

		get.setQueryString(new NameValuePair[] { new NameValuePair("os.name", info.getOsName()), new NameValuePair("os.arch", info.getOsArch()),
				new NameValuePair("os.version", info.getOsVersion()) });

		client.executeMethod(get);

		if(!HttpStatus.OK.equals(get.getStatusCode())) {
			logger.info("Cannot retrieve NexU info from " + infoUrl + ", status code = " + get.getStatusCode());
			return null;
		}
		
		return get.getResponseBody();

	}

}
