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

import java.lang.reflect.Type;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.x509.CertificateToken;

public class CertificateTypeAdapter implements JsonSerializer<CertificateToken>, JsonDeserializer<CertificateToken> {

	@Override
	public CertificateToken deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return DSSUtils.loadCertificateFromBase64EncodedString(json.getAsString());
	}

	@Override
	public JsonElement serialize(CertificateToken src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(Base64.encodeBase64String(src.getEncoded()));
	}
	
}
