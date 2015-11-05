
package lu.nowina.nexu.api;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the lu.nowina.nexu.api package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content. The Java representation of XML content can
 * consist of schema derived interfaces and classes representing the binding of schema type definitions, element declarations and model groups. Factory methods
 * for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _ReportError_QNAME = new QName("http://api.nexu.nowina.lu/", "reportError");
	private final static QName _ReportErrorResponse_QNAME = new QName("http://api.nexu.nowina.lu/", "reportErrorResponse");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: lu.nowina.nexu.api
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link ReportError }
	 * 
	 */
	public ReportError createReportError() {
		return new ReportError();
	}

	/**
	 * Create an instance of {@link ReportErrorResponse }
	 * 
	 */
	public ReportErrorResponse createReportErrorResponse() {
		return new ReportErrorResponse();
	}

	/**
	 * Create an instance of {@link Feedback }
	 * 
	 */
	public Feedback createFeedback() {
		return new Feedback();
	}

	/**
	 * Create an instance of {@link DetectedCard }
	 * 
	 */
	public DetectedCard createDetectedCard() {
		return new DetectedCard();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link ReportError }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "http://api.nexu.nowina.lu/", name = "reportError")
	public JAXBElement<ReportError> createReportError(ReportError value) {
		return new JAXBElement<ReportError>(_ReportError_QNAME, ReportError.class, null, value);
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link ReportErrorResponse }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "http://api.nexu.nowina.lu/", name = "reportErrorResponse")
	public JAXBElement<ReportErrorResponse> createReportErrorResponse(ReportErrorResponse value) {
		return new JAXBElement<ReportErrorResponse>(_ReportErrorResponse_QNAME, ReportErrorResponse.class, null, value);
	}

}
