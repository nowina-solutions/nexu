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
package lu.nowina.nexu.cache;

import java.util.LinkedHashMap;

/**
 * This class implements a FIFO cache whose size is bounded.
 * 
 * <p>When the maximum size is reached, the eldest inserted entry is removed from the cache.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class FIFOCache<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 3868274387687593515L;

	private int maxSize;
	
	public FIFOCache(int maxSize) {
		super();
		this.maxSize = maxSize;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maxSize;
	}
}
