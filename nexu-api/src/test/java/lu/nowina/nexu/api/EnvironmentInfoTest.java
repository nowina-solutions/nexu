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
package lu.nowina.nexu.api;

import org.junit.Assert;
import org.junit.Test;

public class EnvironmentInfoTest {

	@Test
	public void test1() throws Exception {

		EnvironmentInfo info = EnvironmentInfo.buildFromSystemProperties(System.getProperties());

		EnvironmentInfo info2 = EnvironmentInfo.buildFromSystemProperties(System.getProperties());

		Assert.assertTrue(info.matches(info2));
		Assert.assertTrue(info2.matches(info));

	}

	@Test
	public void test2() throws Exception {

		EnvironmentInfo info = EnvironmentInfo.buildFromSystemProperties(System.getProperties());

		EnvironmentInfo infoNull = new EnvironmentInfo();

		Assert.assertTrue(infoNull.matches(info));

	}

	@Test
	public void test3() throws Exception {

		EnvironmentInfo info = EnvironmentInfo.buildFromSystemProperties(System.getProperties());

		EnvironmentInfo info2 = EnvironmentInfo.buildFromSystemProperties(System.getProperties());
		info2.setArch(null);

		Assert.assertFalse(info.matches(info2));
		Assert.assertTrue(info2.matches(info));

	}

	@Test
	public void test4() throws Exception {

		EnvironmentInfo info = EnvironmentInfo.buildFromSystemProperties(System.getProperties());

		EnvironmentInfo info2 = EnvironmentInfo.buildFromSystemProperties(System.getProperties());
		info2.setOs(null);

		Assert.assertFalse(info.matches(info2));
		Assert.assertTrue(info2.matches(info));

	}

}
