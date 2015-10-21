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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import lu.nowina.nexu.ConfigurationException;
import lu.nowina.nexu.TechnicalException;

public class NexuScriptControllerTest {

	@Test(expected=TechnicalException.class)
	public void test1() throws Exception {
		
		NexuScriptController controller = new NexuScriptController();
		controller.loadScript();
		
	}
	
	@Test(expected=ConfigurationException.class)
	public void test3() throws Exception {
		
		NexuScriptController controller = new NexuScriptController();
		controller.postConstruct();
		
	}
	
	@Test
	public void test2() throws Exception {
		
		NexuScriptController controller = new NexuScriptController();
		controller.baseUrl = "http://localhost:8070/";
		controller.nexuUrl = "http://localhost:9876/";
		ResponseEntity<String> resp = controller.loadScript();
		
		Assert.assertEquals(MediaType.parseMediaType("text/javascript"), resp.getHeaders().getContentType());
		Assert.assertTrue(resp.getBody() != null && !resp.getBody().isEmpty());
		
	}
	
}
