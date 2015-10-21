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
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import lu.nowina.nexu.server.manager.SCDatabaseManager;

public class SCDatabaseControllerTest {

	@Test
	public void test1() throws Exception {
		
		SCDatabaseController controller = new SCDatabaseController();
		SCDatabaseManager manager = new SCDatabaseManager();
		manager.setNexuDatabaseFile(new FileSystemResource("src/test/resources/db.xml"));
		controller.databaseManager = manager;
		

		ResponseEntity<byte[]> resp = controller.getDatabase();
		
		Assert.assertEquals(MediaType.parseMediaType("application/xml"), resp.getHeaders().getContentType());
		Assert.assertTrue(resp.getBody() != null);
		System.out.println(new String(resp.getBody()));
	}
	
}
