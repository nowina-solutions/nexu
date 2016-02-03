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
package lu.nowina.nexu.json;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import lu.nowina.nexu.cache.FIFOCache;

/**
 * JUnit test for {@link FIFOCache}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class FIFOCacheTest {

	public FIFOCacheTest() {
		super();
	}

	@Test
	public void test1() {
		final FIFOCache<Integer, Integer> cache = new FIFOCache<Integer, Integer>(1);
		Assert.assertEquals(0, cache.size());
		cache.put(1, 1);
		Assert.assertEquals(1, cache.size());
		cache.put(2, 2);
		Assert.assertEquals(1, cache.size());
		Assert.assertEquals(2, cache.keySet().iterator().next().intValue());
	}
	
	@Test
	public void test2() {
		final FIFOCache<Integer, Integer> cache = new FIFOCache<Integer, Integer>(500);
		int expectedCacheSize = 0;
		Assert.assertEquals(expectedCacheSize, cache.size());
		for(int i = 0; i < 500; ++i) {
			cache.put(i, i);
			Assert.assertEquals(++expectedCacheSize, cache.size());
		}
		cache.put(500, 500);
		Assert.assertEquals(expectedCacheSize, cache.size());
		final TreeMap<Integer, Integer> sortedMap = new TreeMap<Integer, Integer>(cache);
		Assert.assertEquals(expectedCacheSize, sortedMap.size());
		Assert.assertEquals(1, sortedMap.firstKey().intValue());
		Assert.assertEquals(500, sortedMap.lastKey().intValue());
	}
}
