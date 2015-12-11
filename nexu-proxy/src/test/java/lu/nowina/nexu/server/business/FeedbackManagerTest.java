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
package lu.nowina.nexu.server.business;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lu.nowina.nexu.ConfigurationException;
import lu.nowina.nexu.api.Feedback;

public class FeedbackManagerTest {

	@Test(expected = ConfigurationException.class)
	public void test1() throws Exception {

		FeedbackManager endpoint = new FeedbackManager();
		endpoint.postConstruct();
	}

	@Test(expected = ConfigurationException.class)
	public void test2() throws Exception {

		FeedbackManager endpoint = new FeedbackManager();
		endpoint.setRepository("non-existing");
		endpoint.postConstruct();
	}

	@Test
	public void test3() throws Exception {

		FeedbackManager endpoint = new FeedbackManager();
		endpoint.setRepository("target");
		endpoint.postConstruct();
	}

	@Test
	public void test4() throws Exception {

		FeedbackManager endpoint = new FeedbackManager();
		endpoint.setRepository("target");
		endpoint.postConstruct();
		List<FeedbackFile> list = endpoint.feedbackList();
		int size = list.size();

		Feedback f = new Feedback();
		endpoint.reportError(f);

		list = endpoint.feedbackList();
		Assert.assertEquals(size + 1, list.size());
	}

	@Test
	public void test5() throws Exception {

		FeedbackManager endpoint = new FeedbackManager();
		endpoint.setRepository("target");
		endpoint.postConstruct();
		List<FeedbackFile> list = endpoint.feedbackList();

		for (FeedbackFile f : list) {
			Assert.assertNotNull(f.getId());
			Assert.assertNotNull(f.getDate());
		}

	}

}
