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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DelegatedHttpServerRequest implements HttpServletRequest {

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

	public Object getAttribute(String arg0) {
		return wrapped.getAttribute(arg0);
	}

	public Enumeration getAttributeNames() {
		return wrapped.getAttributeNames();
	}

	public String getAuthType() {
		return wrapped.getAuthType();
	}

	public String getCharacterEncoding() {
		return wrapped.getCharacterEncoding();
	}

	public int getContentLength() {
		return wrapped.getContentLength();
	}

	public String getContentType() {
		return wrapped.getContentType();
	}

	public String getContextPath() {
		return context;
	}

	public Cookie[] getCookies() {
		return wrapped.getCookies();
	}

	public long getDateHeader(String arg0) {
		return wrapped.getDateHeader(arg0);
	}

	public String getHeader(String arg0) {
		return wrapped.getHeader(arg0);
	}

	public Enumeration getHeaderNames() {
		return wrapped.getHeaderNames();
	}

	public Enumeration getHeaders(String arg0) {
		return wrapped.getHeaders(arg0);
	}

	public ServletInputStream getInputStream() throws IOException {
		return wrapped.getInputStream();
	}

	public int getIntHeader(String arg0) {
		return wrapped.getIntHeader(arg0);
	}

	public String getLocalAddr() {
		return wrapped.getLocalAddr();
	}

	public String getLocalName() {
		return wrapped.getLocalName();
	}

	public int getLocalPort() {
		return wrapped.getLocalPort();
	}

	public Locale getLocale() {
		return wrapped.getLocale();
	}

	public Enumeration getLocales() {
		return wrapped.getLocales();
	}

	public String getMethod() {
		return wrapped.getMethod();
	}

	public String getParameter(String arg0) {
		return wrapped.getParameter(arg0);
	}

	public Map getParameterMap() {
		return wrapped.getParameterMap();
	}

	public Enumeration getParameterNames() {
		return wrapped.getParameterNames();
	}

	public String[] getParameterValues(String arg0) {
		return wrapped.getParameterValues(arg0);
	}

	public String getPathInfo() {
		return wrapped.getPathInfo().substring(getContextPath().length());
	}

	public String getPathTranslated() {
		return wrapped.getPathTranslated();
	}

	public String getProtocol() {
		return wrapped.getProtocol();
	}

	public String getQueryString() {
		return wrapped.getQueryString();
	}

	public BufferedReader getReader() throws IOException {
		return wrapped.getReader();
	}

	public String getRealPath(String arg0) {
		return wrapped.getRealPath(arg0);
	}

	public String getRemoteAddr() {
		return wrapped.getRemoteAddr();
	}

	public String getRemoteHost() {
		return wrapped.getRemoteHost();
	}

	public int getRemotePort() {
		return wrapped.getRemotePort();
	}

	public String getRemoteUser() {
		return wrapped.getRemoteUser();
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		return wrapped.getRequestDispatcher(arg0);
	}

	public String getRequestURI() {
		return wrapped.getRequestURI();
	}

	public StringBuffer getRequestURL() {
		return wrapped.getRequestURL();
	}

	public String getRequestedSessionId() {
		return wrapped.getRequestedSessionId();
	}

	public String getScheme() {
		return wrapped.getScheme();
	}

	public String getServerName() {
		return wrapped.getServerName();
	}

	public int getServerPort() {
		return wrapped.getServerPort();
	}

	public String getServletPath() {
		return wrapped.getServletPath();
	}

	public HttpSession getSession() {
		return wrapped.getSession();
	}

	public HttpSession getSession(boolean arg0) {
		return wrapped.getSession(arg0);
	}

	public Principal getUserPrincipal() {
		return wrapped.getUserPrincipal();
	}

	public boolean isRequestedSessionIdFromCookie() {
		return wrapped.isRequestedSessionIdFromCookie();
	}

	public boolean isRequestedSessionIdFromURL() {
		return wrapped.isRequestedSessionIdFromURL();
	}

	public boolean isRequestedSessionIdFromUrl() {
		return wrapped.isRequestedSessionIdFromUrl();
	}

	public boolean isRequestedSessionIdValid() {
		return wrapped.isRequestedSessionIdValid();
	}

	public boolean isSecure() {
		return wrapped.isSecure();
	}

	public boolean isUserInRole(String arg0) {
		return wrapped.isUserInRole(arg0);
	}

	public void removeAttribute(String arg0) {
		wrapped.removeAttribute(arg0);
	}

	public void setAttribute(String arg0, Object arg1) {
		wrapped.setAttribute(arg0, arg1);
	}

	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		wrapped.setCharacterEncoding(arg0);
	}

}
