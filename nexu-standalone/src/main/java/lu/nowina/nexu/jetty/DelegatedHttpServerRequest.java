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
package lu.nowina.nexu.jetty;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import lu.nowina.nexu.api.plugin.HttpRequest;

public class DelegatedHttpServerRequest implements HttpRequest {

	private HttpServletRequest wrapped;

	private String context;

	public DelegatedHttpServerRequest(HttpServletRequest delegate, String context) {
		this.wrapped = delegate;

		String ctx = null;
		if (context.startsWith("/")) {
			ctx = context;
		} else {
			ctx = "/" + context;
		}
		this.context = ctx;
	}

	@Override
	public String getTarget() {
		return wrapped.getPathInfo().substring(context.length());
	}

	@Override
	public InputStream getInputStream() {
		try {
			return wrapped.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getParameter(String name) {
		return wrapped.getParameter(name);
	}

}
