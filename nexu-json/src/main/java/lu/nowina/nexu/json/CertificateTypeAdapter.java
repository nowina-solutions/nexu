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
