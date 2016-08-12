package com.github.log.filter.wrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.io.IOUtils;

import org.apache.tomcat.util.http.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WrapperedRequest extends HttpServletRequestWrapper {

	private boolean parametersParsed = false;

	private byte[] byteArray = null;

	private static final Logger logger = LoggerFactory.getLogger(WrapperedRequest.class);

	public WrapperedRequest(HttpServletRequest request) throws IOException {
		super(request);
		byteArray = IOUtils.toByteArray(request.getInputStream());
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
		return new ServletInputStream() {

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) {

			}

			@Override
			public int read() throws IOException {
				return inputStream.read();
			}

		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	public byte[] getByteArray() {
		return byteArray;
	}

	public void setByteArray(byte[] byteArray) {
		this.byteArray = byteArray;
	}

	/**
	 * Parse request parameters.
	 */
	protected void parseParameters() {
		try {
			parametersParsed = true;
			RequestFacade requestFacade = (RequestFacade) this.getRequest();
			Field requestFiled = requestFacade.getClass().getDeclaredField("request");
			requestFiled.setAccessible(true);

			Request request = (Request) requestFiled.get(requestFacade);
			Class<? extends Request> requestClass = request.getClass();

			Field parametersParsedF = requestClass.getDeclaredField("parametersParsed");
			parametersParsedF.setAccessible(true);
			parametersParsedF.set(request, true);

			Field coyoteRequestField = requestClass.getDeclaredField("coyoteRequest");
			coyoteRequestField.setAccessible(true);
			org.apache.coyote.Request coyoteRequest = (org.apache.coyote.Request) coyoteRequestField.get(request);

			Parameters parameters = coyoteRequest.getParameters();

			Connector connector = request.getConnector();
			boolean success = false;
			try {
				// Set this every time in case limit has been changed via JMX
				parameters.setLimit(request.getConnector().getMaxParameterCount());

				// getCharacterEncoding() may have been overridden to search for
				// hidden form field containing request encoding
				String enc = getCharacterEncoding();
				boolean useBodyEncodingForURI = connector.getUseBodyEncodingForURI();
				if (enc != null) {
					parameters.setEncoding(enc);
					if (useBodyEncodingForURI) {
						parameters.setQueryStringEncoding(enc);
					}
				} else {
					parameters.setEncoding(org.apache.coyote.Constants.DEFAULT_CHARACTER_ENCODING);
					if (useBodyEncodingForURI) {
						parameters.setQueryStringEncoding(org.apache.coyote.Constants.DEFAULT_CHARACTER_ENCODING);
					}
				}

				parameters.handleQueryParameters();

				Method isParseBodyMethod = connector.getClass().getDeclaredMethod("isParseBodyMethod",String.class);
				isParseBodyMethod.setAccessible(true);
				Boolean invoke = (Boolean) isParseBodyMethod.invoke(connector, request.getMethod());
				if (!invoke) {
					success = true;
					return;
				}

				String contentType = getContentType();
				if (contentType == null) {
					contentType = "";
				}
				int semicolon = contentType.indexOf(';');
				if (semicolon >= 0) {
					contentType = contentType.substring(0, semicolon).trim();
				} else {
					contentType = contentType.trim();
				}

				if ("multipart/form-data".equals(contentType)) {
					Method parseParts = requestClass.getDeclaredMethod("parseParts",boolean.class);
					parseParts.setAccessible(true);
					parseParts.invoke(request, false);
					success = true;
					return;
				}

				if (!("application/x-www-form-urlencoded".equals(contentType))) {
					success = true;
					return;
				}

				int len = getContentLength();

				if (len > 0) {
					int maxPostSize = connector.getMaxPostSize();
					if ((maxPostSize > 0) && (len > maxPostSize)) {
						Context context = request.getContext();
						if (context != null && context.getLogger().isDebugEnabled()) {
							context.getLogger().debug("coyoteRequest.postTooLarge");
						}
						Method checkSwallowInputMethod = requestClass.getDeclaredMethod("checkSwallowInput");
						checkSwallowInputMethod.setAccessible(true);
						checkSwallowInputMethod.invoke(request);
						return;
					}
					byte[] formData = byteArray;
					parameters.processParameters(formData, 0, len);
				} else if ("chunked".equalsIgnoreCase(coyoteRequest.getHeader("transfer-encoding"))) {
					byte[] formData = byteArray;
					if (formData != null) {
						parameters.processParameters(formData, 0, formData.length);
					}
				}
				success = true;
			} finally {
				if (!success) {
					parameters.setParseFailed(true);
				}
			}
		} catch (Exception e) {
			logger.error("解析request出错,原因{}",e.getMessage(),e);
			e.printStackTrace();
		}

	}

	public void test(){
		System.err.println(getParameter("username"));

		System.err.println(getCharacterEncoding());
		System.err.println(getContentLength());
		System.err.println(getContentType());
		Enumeration<String> headerNames = getHeaderNames();
		while(headerNames.hasMoreElements()){
			String nextElement = headerNames.nextElement();
			System.err.println(nextElement+"---->"+getHeader(nextElement));
		}
		System.err.println(getPathInfo());
		System.err.println(getPathTranslated());
		System.err.println(getProtocol());
		System.err.println(getDispatcherType());
		System.err.println(getRequestedSessionId());
		Cookie[] cookies = getCookies();
		for(Cookie cookie : cookies){
			System.err.println(cookie.getName()+"--->"+ cookie.getValue());
		}
		Enumeration<String> attributeNames = getAttributeNames();
		while(attributeNames.hasMoreElements()){
			String nextElement = attributeNames.nextElement();
			System.err.println(nextElement+"---->"+getAttribute(nextElement));
		}
		System.err.println(getServerName());
		System.err.println(getRequestURL());
		System.err.println(getScheme());
		System.err.println(getServletPath());
	}


	@Override
	public Enumeration<String> getParameterNames() {
		if (!parametersParsed) {
			parseParameters();
		}
		return super.getParameterNames();
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		if (!parametersParsed) {
			parseParameters();
		}
		return super.getParameterMap();
	}

	@Override
	public String getParameter(String name) {
		if (!parametersParsed) {
			parseParameters();
		}
		return super.getParameter(name);
	}

	@Override
	public String[] getParameterValues(String name) {
		if (!parametersParsed) {
			parseParameters();
		}
		return super.getParameterValues(name);
	}
}
