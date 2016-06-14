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
package lu.nowina.nexu.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author axel.abinet
 *
 */
public class WebUtilities {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebUtilities.class);
	
	private static final Pattern IPV4_PATTERN = Pattern.compile("(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])");
	private static final Pattern IPV6_PATTERN = Pattern.compile("([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}");
	
	public static boolean isIpAddress(String address) {
	    Matcher ipv4Matcher = IPV4_PATTERN.matcher(address);
	    Matcher ipv6Matcher = IPV6_PATTERN.matcher(address);
	    return ipv4Matcher.matches() || ipv6Matcher.matches();
	}
	
	public static String resolveIp(String hostName) {
		try {
			InetAddress address = InetAddress.getByName(hostName);
			return address.getHostAddress();
		} catch (UnknownHostException e) {
			LOGGER.warn("Unknown host " + hostName);
			return null;
		}
	}
	
	public static String resolveHostName(String hostAddress) {
		try {
			InetAddress address = InetAddress.getByName(hostAddress);
			return address.getHostName();
		} catch (UnknownHostException e) {
			LOGGER.warn("Unknown address " + hostAddress);
			return null;
		}
	}
}
