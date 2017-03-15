package com.eurodyn.qlack2.be.forms.proxy.servlet;

import com.eurodyn.qlack2.be.forms.proxy.api.ConfigService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.HeaderGroup;
import org.apache.http.util.EntityUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProxySelector;
import java.net.URI;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyServlet extends HttpServlet {

	private static final long serialVersionUID = -7558329235043795776L;

	private static final Logger LOGGER = Logger.getLogger(ProxyServlet.class
			.getName());

	private ConfigService configService;

	/* INIT PARAMETER NAME CONSTANTS */

	/** The parameter name for the target (destination) URI to proxy to. */
	private static final String P_TARGET_URI = "targetUri";

	/* MISC */

	protected boolean doForwardIP = true;
	/** User agents shouldn't send the url fragment but what if it does? */
	protected boolean doSendUrlFragment = true;
	protected boolean doEnableRedirects = false;
	protected URI targetUriObj;
	/** targetUriObj.toString() */
	protected String targetUri;
	protected String proxy;
	protected HttpClient proxyClient;

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	@Override
	public String getServletInfo() {
		return "A servlet for proxying orbeon requests";
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		try {
			proxy = servletConfig.getInitParameter("proxy");
			targetUriObj = new URI(servletConfig.getInitParameter(P_TARGET_URI));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Trying to process targetUri init parameter", e);
			throw new RuntimeException(
					"Trying to process targetUri init parameter: " + e, e);
		}
		targetUri = targetUriObj.toString();

		proxyClient = createHttpClient();
	}

	/**
	 * Creation of http client
	 *
	 * @return
	 */
	private HttpClient createHttpClient() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setRedirectsEnabled(doEnableRedirects).build();
		SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(
				ProxySelector.getDefault());
		CloseableHttpClient httpClient = HttpClients.custom()
				.setRoutePlanner(routePlanner)
				.setDefaultRequestConfig(requestConfig).build();

		return httpClient;
	}

	@Override
	public void destroy() {
		// As of HttpComponents v4.3, clients implement closeable
		if (proxyClient instanceof AutoCloseable) {
			try {
				((Closeable) proxyClient).close();
			} catch (IOException e) {
				log("While destroying servlet, shutting down httpclient: " + e,
						e);
			}
		}
		super.destroy();
	}

	@Override
	protected void service(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) throws ServletException,
			IOException {
		LOGGER.log(Level.FINE, "Handling a proxied request.");
		// Make the Request
		// note: we won't transfer the protocol version because I'm not sure it
		// would truly be compatible
		String method = servletRequest.getMethod();

		// Special handling for Cross-Origin Resource Sharing (CORS)
		if (method.equals("OPTIONS")) {
			servletResponse.setStatus(200);

			String originHeader = servletRequest.getHeader("Origin");
			if (originHeader != null && !StringUtils.isEmpty(originHeader)) {

				if (configService.getAllowedOriginUris().isEmpty()
						|| (!configService.getAllowedOriginUris().isEmpty() && configService
								.getAllowedOriginUris().contains(originHeader))) {
					servletResponse.setHeader("Access-Control-Allow-Origin",
							originHeader);

					String allowedMethodsHeader = servletRequest
							.getHeader("Access-Control-Request-Method");
					if (allowedMethodsHeader != null
							&& !StringUtils.isEmpty(allowedMethodsHeader)) {
						servletResponse.setHeader(
								"Access-Control-Allow-Methods",
								allowedMethodsHeader);
					}

					String allowedHeadersHeader = servletRequest
							.getHeader("Access-Control-Request-Headers");
					if (allowedHeadersHeader != null
							&& !StringUtils.isEmpty(allowedHeadersHeader)) {
						servletResponse.setHeader(
								"Access-Control-Allow-Headers",
								allowedHeadersHeader);
					}
				}
			}

			return;
		}

		String serverName = servletRequest.getServerName();
		int serverPort = servletRequest.getServerPort();

		String proxyRequestUri = rewriteUrlFromRequest(servletRequest);

		HttpRequest proxyRequest;
		// spec: RFC 2616, sec 4.3: either of these two headers signal that
		// there is a message body.
		if (servletRequest.getHeader(HttpHeaders.CONTENT_LENGTH) != null
				|| servletRequest.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {
			HttpEntityEnclosingRequest eProxyRequest = new BasicHttpEntityEnclosingRequest(
					method, proxyRequestUri);
			// Add the input entity (streamed)
			// note: we don't bother ensuring we close the servletInputStream
			// since the container handles it
			eProxyRequest.setEntity(new InputStreamEntity(servletRequest
					.getInputStream(), servletRequest.getContentLength()));
			proxyRequest = eProxyRequest;
		} else {
			proxyRequest = new BasicHttpRequest(method, proxyRequestUri);
		}

		copyRequestHeaders(servletRequest, proxyRequest);

		setXForwardedForHeader(servletRequest, proxyRequest);

		HttpResponse proxyResponse = null;
		try {
			// Execute the request
			LOGGER.log(Level.FINEST, "Proxy {0} uri: {1} -- {2}", new Object[] {
					method, servletRequest.getRequestURI(),
					proxyRequest.getRequestLine().getUri() });

			proxyResponse = proxyClient.execute(
					URIUtils.extractHost(targetUriObj), proxyRequest);

			// Process the response
			int statusCode = proxyResponse.getStatusLine().getStatusCode();

			if (doResponseRedirectOrNotModifiedLogic(servletRequest,
					servletResponse, proxyResponse, statusCode)) {
				// the response is already "committed" now without any body to
				// send
				// TODO copy response headers?
				return;
			}

			// Pass the response code. This method with the "reason phrase" is
			// deprecated but it's the only way to pass the
			// reason along too.
			// noinspection deprecation
			servletResponse.setStatus(statusCode, proxyResponse.getStatusLine()
					.getReasonPhrase());

			copyResponseHeaders(proxyResponse, servletResponse);

			// Special handling for Cross-Origin Resource Sharing (CORS). In
			// case the initial request has the Origin header, then the
			// Access-Control-Allow-Origin header should be added to the
			// response
			handleOriginHeader(servletRequest, servletResponse);

			// Send the content to the client
			copyResponseEntity(proxyResponse, servletResponse, serverName,
					serverPort);

		} catch (Exception e) {
			// abort request, according to best practice with HttpClient
			if (proxyRequest instanceof AbortableHttpRequest) {
				AbortableHttpRequest abortableHttpRequest = (AbortableHttpRequest) proxyRequest;
				abortableHttpRequest.abort();
			}
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			if (e instanceof ServletException)
				throw (ServletException) e;
			// noinspection ConstantConditions
			if (e instanceof IOException)
				throw (IOException) e;
			throw new RuntimeException(e);

		} finally {
			// make sure the entire entity was consumed, so the connection is
			// released
			consumeQuietly(proxyResponse.getEntity());
			closeQuietly(servletResponse.getOutputStream());
		}
	}

	protected boolean doResponseRedirectOrNotModifiedLogic(
			HttpServletRequest servletRequest,
			HttpServletResponse servletResponse, HttpResponse proxyResponse,
			int statusCode) throws ServletException, IOException {
		// Check if the proxy response is a redirect
		// The following code is adapted from
		// org.tigris.noodle.filters.CheckForRedirect
		if (statusCode >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */
				&& statusCode < HttpServletResponse.SC_NOT_MODIFIED /* 304 */) {
			Header locationHeader = proxyResponse
					.getLastHeader(HttpHeaders.LOCATION);
			if (locationHeader == null) {
				throw new ServletException("Received status code: "
						+ statusCode + " but no " + HttpHeaders.LOCATION
						+ " header was found in the response");
			}
			// Modify the redirect to go to this proxy servlet rather that the
			// proxied host
			String locStr = rewriteUrlFromResponse(servletRequest,
					locationHeader.getValue());

			servletResponse.sendRedirect(locStr);
			return true;
		}
		// 304 needs special handling. See:
		// http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
		// We get a 304 whenever passed an 'If-Modified-Since'
		// header and the data on disk has not changed; server
		// responds w/ a 304 saying I'm not going to send the
		// body because the file has not changed.
		if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
			servletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
			servletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return true;
		}
		return false;
	}

	protected void closeQuietly(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			log(e.getMessage(), e);
		}
	}

	/**
	 * HttpClient v4.1 doesn't have the
	 * {@link org.apache.http.util.EntityUtils#consumeQuietly(org.apache.http.HttpEntity)}
	 * method.
	 */
	protected void consumeQuietly(HttpEntity entity) {
		try {
			EntityUtils.consume(entity);
		} catch (IOException e) {// ignore
			log(e.getMessage(), e);
		}
	}

	/**
	 * These are the "hop-by-hop" headers that should not be copied.
	 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html I use an
	 * HttpClient HeaderGroup class instead of Set because this approach
	 * does case insensitive lookup faster.
	 */
	protected static final HeaderGroup hopByHopHeaders;
	static {
		hopByHopHeaders = new HeaderGroup();
		String[] headers = new String[] { "Connection", "Keep-Alive",
				"Proxy-Authenticate", "Proxy-Authorization", "TE", "Trailers",
				"Transfer-Encoding", "Upgrade" };
		for (String header : headers) {
			hopByHopHeaders.addHeader(new BasicHeader(header, null));
		}
	}

	/** Copy request headers from the servlet client to the proxy request. */
	protected void copyRequestHeaders(HttpServletRequest servletRequest,
			HttpRequest proxyRequest) {
		// Get an Enumeration of all of the header names sent by the client
		Enumeration enumerationOfHeaderNames = servletRequest.getHeaderNames();
		while (enumerationOfHeaderNames.hasMoreElements()) {
			String headerName = (String) enumerationOfHeaderNames.nextElement();
			// Instead the content-length is effectively set via
			// InputStreamEntity
			if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH))
				continue;
			if (hopByHopHeaders.containsHeader(headerName))
				continue;

			Enumeration headers = servletRequest.getHeaders(headerName);
			while (headers.hasMoreElements()) {// sometimes more than one value
				String headerValue = (String) headers.nextElement();
				// In case the proxy host is running multiple virtual servers,
				// rewrite the Host header to ensure that we get content from
				// the correct virtual server
				if (headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
					HttpHost host = URIUtils.extractHost(this.targetUriObj);
					headerValue = host.getHostName();
					if (host.getPort() != -1)
						headerValue += ":" + host.getPort();
				}
				proxyRequest.addHeader(headerName, headerValue);
			}
		}
	}

	private void setXForwardedForHeader(HttpServletRequest servletRequest,
			HttpRequest proxyRequest) {
		String headerName = "X-Forwarded-For";
		if (doForwardIP) {
			String newHeader = servletRequest.getRemoteAddr();
			String existingHeader = servletRequest.getHeader(headerName);
			if (existingHeader != null) {
				newHeader = existingHeader + ", " + newHeader;
			}
			proxyRequest.setHeader(headerName, newHeader);
		}
	}

	/** Copy proxied response headers back to the servlet client. */
	protected void copyResponseHeaders(HttpResponse proxyResponse,
			HttpServletResponse servletResponse) {
		for (Header header : proxyResponse.getAllHeaders()) {
			if (hopByHopHeaders.containsHeader(header.getName()))
				continue;
			servletResponse.addHeader(header.getName(), header.getValue());
		}
	}

	private void handleOriginHeader(HttpServletRequest servletRequest,
			HttpServletResponse servletResponse) {
		String originHeader = servletRequest.getHeader("Origin");

		// In case the initial request was an OPTION request, then the
		// subsequent request contains the Origin header. The response should
		// contain the Access-Control-Allow-Origin header.
		if (originHeader != null && !StringUtils.isEmpty(originHeader)) {
			if (configService.getAllowedOriginUris().isEmpty()
					|| (!configService.getAllowedOriginUris().isEmpty() && configService
							.getAllowedOriginUris().contains(originHeader))) {
				servletResponse.setHeader("Access-Control-Allow-Origin",
						originHeader);
			}
		}
	}

	/**
	 * Copy response body data (the entity) from the proxy to the servlet
	 * client.
	 *
	 * @param proxyResponse
	 * @param servletResponse
	 * @param serverPort
	 * @param serverName
	 */
	protected void copyResponseEntity(HttpResponse proxyResponse,
			HttpServletResponse servletResponse, String serverName,
			int serverPort) throws IOException {
		HttpEntity entity = proxyResponse.getEntity();
		if (entity != null) {
			OutputStream servletOutputStream = servletResponse
					.getOutputStream();

			ContentType contentType = ContentType.get(entity);

			if (contentType != null && contentType.getMimeType() != null
					&& contentType.getMimeType().equalsIgnoreCase("text/html")) {
				InputStream stream = entity.getContent();

				try {
					// Convert entities of content type "text/html" to String,
					// so that url rewriting can be performed
					String content = IOUtils.toString(stream, "UTF-8");

					// Rewrite URL starting with "/orbeon to "/qbe-proxy. This
					// way all direct requests to orbeon can be avoided.
					content = StringUtils.replace(content, "\"/orbeon/", proxy);
					servletOutputStream.write(content.getBytes("UTF-8"));
				} finally {
					stream.close();
				}
			} else {
				entity.writeTo(servletOutputStream);
			}
		}

	}

	/**
	 * Reads the request URI from {@code servletRequest} and rewrites it,
	 * considering {@link #targetUriObj}. It's used to make the new request.
	 */
	protected String rewriteUrlFromRequest(HttpServletRequest servletRequest) {
		StringBuilder uri = new StringBuilder(500);
		uri.append(targetUri);
		// Handle the path given to the servlet
		if (servletRequest.getPathInfo() != null) {// ex: /my/path.html
			uri.append(encodeUriQuery(servletRequest.getPathInfo()));
		}
		// Handle the query string
		String queryString = servletRequest.getQueryString();// ex:(following
																// '?'):
																// name=value&foo=bar#fragment
		if (queryString != null && queryString.length() > 0) {
			uri.append('?');
			int fragIdx = queryString.indexOf('#');
			String queryNoFrag = (fragIdx < 0 ? queryString : queryString
					.substring(0, fragIdx));
			uri.append(encodeUriQuery(queryNoFrag));
			if (doSendUrlFragment && fragIdx >= 0) {
				uri.append('#');
				uri.append(encodeUriQuery(queryString.substring(fragIdx + 1)));
			}
		}
		return uri.toString();
	}

	/**
	 * For a redirect response from the target server, this translates
	 * {@code theUrl} to redirect to and translates it to one the original
	 * client can use.
	 */
	protected String rewriteUrlFromResponse(HttpServletRequest servletRequest,
			String theUrl) {
		// TODO document example paths
		if (theUrl.startsWith(targetUri)) {
			String curUrl = servletRequest.getRequestURL().toString();// no
																		// query
			String pathInfo = servletRequest.getPathInfo();
			if (pathInfo != null) {
				assert curUrl.endsWith(pathInfo);
				curUrl = curUrl.substring(0,
						curUrl.length() - pathInfo.length());// take pathInfo
																// off
			}
			theUrl = curUrl + theUrl.substring(targetUri.length());
		}
		return theUrl;
	}

	/** The target URI as configured. Not null. */
	public String getTargetUri() {
		return targetUri;
	}

	/**
	 * Encodes characters in the query or fragment part of the URI.
	 *
	 * <p>
	 * Unfortunately, an incoming URI sometimes has characters disallowed by the
	 * spec. HttpClient insists that the outgoing proxied request has a valid
	 * URI because it uses Java's {@link URI}. To be more forgiving, we must
	 * escape the problematic characters. See the URI class for the spec.
	 *
	 * @param in example name=value&amp;foo=bar#fragment
	 */
	protected static CharSequence encodeUriQuery(CharSequence in) {
		// Note that I can't simply use URI.java to encode because it will
		// escape pre-existing escaped things.
		StringBuilder outBuf = null;
		Formatter formatter = null;
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			boolean escape = true;
			if (c < 128) {
				if (asciiQueryChars.get(c)) {
					escape = false;
				}
			} else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {// not-ascii
				escape = false;
			}
			if (!escape) {
				if (outBuf != null)
					outBuf.append(c);
			} else {
				// escape
				if (outBuf == null) {
					outBuf = new StringBuilder(in.length() + 5 * 3);
					outBuf.append(in, 0, i);
					formatter = new Formatter(outBuf);
				}
				// leading %, 0 padded, width 2, capital hex
				formatter.format("%%%02X", (int) c);// TODO
			}
		}
		return outBuf != null ? outBuf : in;
	}

	protected static final BitSet asciiQueryChars;
	static {
		char[] c_unreserved = "_-!.~'()*".toCharArray();// plus alphanum
		char[] c_punct = ",;:$&+=".toCharArray();
		char[] c_reserved = "?/[]@".toCharArray();// plus punct

		asciiQueryChars = new BitSet(128);
		for (char c = 'a'; c <= 'z'; c++)
			asciiQueryChars.set(c);
		for (char c = 'A'; c <= 'Z'; c++)
			asciiQueryChars.set(c);
		for (char c = '0'; c <= '9'; c++)
			asciiQueryChars.set(c);
		for (char c : c_unreserved)
			asciiQueryChars.set(c);
		for (char c : c_punct)
			asciiQueryChars.set(c);
		for (char c : c_reserved)
			asciiQueryChars.set(c);

		asciiQueryChars.set('%');// leave existing percent escapes in place
	}

}
