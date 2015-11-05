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
package lu.nowina.nexu.api.plugin;

public class HttpResponse {

	private String content;

	private String contentType;

	private HttpStatus httpStatus;

	public HttpResponse(String content, String contentType, HttpStatus status) {
		this.content = content;
		this.contentType = contentType;

		if (status == null) {
			throw new IllegalArgumentException();
		}
		this.httpStatus = status;
	}

	public String getContent() {
		return content;
	}

	public String getContentType() {
		return contentType;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

}
