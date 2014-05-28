package nz.ac.tuakiri.security.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import au.com.bytecode.opencsv.CSVWriter;
import nz.ac.tuakiri.myproxy.oa4mp.server.servlet.ShibUsernameTransformerConfig;
import edu.uiuc.ncsa.myproxy.oa4mp.server.servlet.AbstractAuthorizationServlet;
import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.servlet.UsernameTransformer;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

public class ShibUsernameTransformer implements UsernameTransformer, Logable {
	public static final String AUTHORIZATION_ATTRIBUTE_MAP_VALUE = "attributeMap";

	private MyLoggingFacade logger;
	private ShibUsernameTransformerConfig config;

	public ShibUsernameTransformer(ShibUsernameTransformerConfig config,
			MyLoggingFacade logger) {
		this.config = config;
		this.logger = logger;

		debug("ShibUsernameTransformer");
	}

	public String createUsername(HttpServletRequest request) {
		String userName = null;
		Map<String, String> attributeMap = null;
		if (config.getAttributesToSend() != null) {
			info("ShibUsernameTransformer: building username from request attributes...");

			List<String> attributes = new ArrayList<String>();
			attributeMap = new HashMap<String, String>();
			for (String attribute : config.getAttributesToSend()) {
				String attributeValue = request.getHeader(attribute);
				if (attributeValue != null) {
					attributes.add(attribute);
					attributes.add(attributeValue);

					attributeMap.put(escapeHtml(attribute),
							escapeHtml(attributeValue));

					info("ShibUsernameTransformer: found value for request attribute '" + attribute
							+ "': '" + attributeValue + "'");
				} else {
					info("ShibUsernameTransformer: header attribute '" + attribute
							+ "' not present in request");
				}
			}

			try {
				userName = getCsv(attributes);
			} catch (IOException e) {
				throw new GeneralException(
						"ShibUsernameTransformer Error: Failure getting username from attributes.", e);
			}

			info("ShibUsernameTransformer: built username: " + userName);
		}

		if (isEmpty(userName)) {
			if (config.isRequireHeader()) {
				throw new GeneralException(
						"ShibUsernameTransformer Error: Could not build user name from attributes.");
			}

		} else {
			request.setAttribute(AUTHORIZATION_ATTRIBUTE_MAP_VALUE,
					attributeMap);
		}
		
		request.setAttribute(
				AbstractAuthorizationServlet.AUTHORIZATION_USER_NAME_VALUE,
				escapeHtml(userName));

		if (config.isMyproxyWorkaround()) {
			userName = "/" + userName;
			
			info("ShibUsernameTransformer: applied MyProxy workaround: user name = " + userName);
		}

		return userName;
	}

	protected MyLoggingFacade getLogger() {
		if (logger == null) {
			logger = new MyLoggingFacade("oa4mp-shibboleth");
		}
		return logger;
	}

	public boolean isDebugOn() {
		return getLogger().isDebugOn();
	}

	public void setDebugOn(boolean setOn) {
		getLogger().setDebugOn(setOn);
	}

	public void debug(String x) {
		getLogger().debug(x);
	}

	public void error(String x) {
		getLogger().error(x);
	}

	public void info(String x) {
		getLogger().info(x);
	}

	public void warn(String x) {
		getLogger().warn(x);
	}

	@Override
	public String createReturnedUsername(HttpServletRequest request,
			String myproxyUsername) {
		debug("ShibUsernameTransformer.createReturnedUsername");

		return myproxyUsername;
	}

	private String getCsv(List<String> attributes) throws IOException {
		StringWriter attributeWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(attributeWriter);
		writer.writeNext(attributes.toArray(new String[0]));
		writer.close();
		return attributeWriter.toString();
	}

	protected boolean isEmpty(String x) {
		return x == null || x.length() == 0;
	}

	@Override
	public String createMyProxyUsername(HttpServletRequest request) {
		debug("ShibUsernameTransformer.createMyProxyUsername");
		
		if (!isProcessDuringStart()) {
			return createUsername(request);
		} else {
			return null;
		}
	}

	public boolean isProcessDuringStart() {
		return config.isProcessDuringStart();
	}

	public boolean isReturnDnAsUsername() {
		return config.isReturnDnAsUsername();
	}

}
