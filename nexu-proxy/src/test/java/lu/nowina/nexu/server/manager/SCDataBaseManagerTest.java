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
package lu.nowina.nexu.server.manager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import lu.nowina.nexu.ConfigurationException;
import lu.nowina.nexu.server.config.OverrideConfig;
import lu.nowina.nexu.server.config.ServiceConfig;
import lu.nowina.nexu.server.config.WebConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class,
  classes={ServiceConfig.class, WebConfig.class, OverrideConfig.class})
public class SCDataBaseManagerTest {

	@Autowired
	private SCDatabaseManager manager;
	
	@Test
	public void test1() {
		final SCDatabaseManager manager = new SCDatabaseManager();
		manager.setNexuDatabaseFile(new FileSystemResource("target/non-existing.xml"));
		manager.postConstruct();
		Assert.assertEquals("d41d8cd98f00b204e9800998ecf8427e", manager.getDatabaseDigest());
	}

	@Test
	public void test2() {
		Assert.assertEquals("78aed59cb9db6d5e176b1eecab86f96d", manager.getDatabaseDigest());
	}

	@Test(expected = ConfigurationException.class)
	public void test3() {
		final SCDatabaseManager manager = new SCDatabaseManager();
		manager.postConstruct();
	}

}
