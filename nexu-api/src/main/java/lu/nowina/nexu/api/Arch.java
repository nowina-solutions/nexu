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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlType(name = "osArch")
@XmlEnum
public enum Arch {

	X86, AMD64, NOT_RECOGNIZED;

	private static final Logger logger = LoggerFactory.getLogger(Arch.class);

	public static Arch forOSArch(String osArch) {
		if ("amd64".equals(osArch) || "x86_64".equals(osArch)) {
			return AMD64;
		} else if ("x86".equals(osArch)) {
			return X86;
		} else {
			logger.warn("Arch not recognized " + osArch);
			return Arch.NOT_RECOGNIZED;
		}
	}
}
