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

/**
 * Enumerates OS detected by NexU
 * 
 * @author David Naramski
 *
 */
@XmlType(name = "osName")
@XmlEnum
public enum OS {

	MACOSX("DYLIB", "*.dylib"),
	LINUX("SO", "*.so"),
	WINDOWS("DLL", "*.dll"),
	NOT_RECOGNIZED("", "");

	private static final Logger LOGGER = LoggerFactory.getLogger(OS.class);

	public static OS forOSName(String osName) {
		if (osName.startsWith("Mac")) {
			return MACOSX;
		} else if (osName.toLowerCase().contains("windows")) {
			return WINDOWS;
		} else if (osName.toLowerCase().contains("linux")) {
			return LINUX;
		} else {
			LOGGER.warn("OS name not recognized " + osName);
			return NOT_RECOGNIZED;
		}
	}

	private final String nativeLibraryFileExtensionDescription;
	private final String nativeLibraryFileExtension;
	
	private OS(final String nativeLibraryFileExtensionDescription, final String nativeLibraryFileExtension) {
		this.nativeLibraryFileExtensionDescription = nativeLibraryFileExtensionDescription;
		this.nativeLibraryFileExtension = nativeLibraryFileExtension;
	}

	public String getNativeLibraryFileExtensionDescription() {
		return nativeLibraryFileExtensionDescription;
	}

	public String getNativeLibraryFileExtension() {
		return nativeLibraryFileExtension;
	}
}
