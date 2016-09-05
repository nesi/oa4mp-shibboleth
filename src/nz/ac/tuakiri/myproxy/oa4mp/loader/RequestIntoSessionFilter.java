/**
 * 
 */
package nz.ac.tuakiri.myproxy.oa4mp.loader;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author malanrw
 * Tuakiri input is coming in as request parameters
 * Since requests internally get forwarded their input parameters are no longer visible.
 * this filter saves them in the session
 */
public class RequestIntoSessionFilter implements Filter {
	
	public final static String LAST_REQUEST_PARAMS = "last_request_params";
	public final static String LAST_REQUEST_HEADERS = "last_request_headers";

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//No-op
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//Capture the request parameters into the session
		HttpSession session = ((HttpServletRequest)request).getSession(true);
		synchronized (session) {
			process (session, request, response, chain);
		}
	}

	/**
	 * Ensure only one request per user session gets processed at a time
	 * @param session
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	private synchronized void process(HttpSession session, 
			ServletRequest request, ServletResponse response, 
			FilterChain chain)
			throws IOException, ServletException {

		//Add to existing input parameters if any
		Map <String,String[]> inparams = request.getParameterMap();
		Map <String,String[]> params = (Map <String,String[]>)session.getAttribute(LAST_REQUEST_PARAMS);
		if (params == null) {
			params = new HashMap <String,String[]>();
			session.setAttribute(LAST_REQUEST_PARAMS, params);
		}
		params.putAll(inparams);
		
		//Add to existing input headers if any
		Enumeration<String> headers = ((HttpServletRequest)request).getHeaderNames();
		Map <String,String> headerMap = (Map <String,String>)session.getAttribute(LAST_REQUEST_HEADERS);
		if (headerMap == null) {
			headerMap = new HashMap <String,String>();
			session.setAttribute(LAST_REQUEST_HEADERS, headerMap);
		}
		while (headers.hasMoreElements()) {
			String headername = headers.nextElement();
			headerMap.put(headername, ((HttpServletRequest)request).getHeader(headername));
		}
		
		int headerMapSizeBefore = headerMap.size();
		int paramsMapSizeBefore = params.size();
		System.out.println("Before forward had "+headerMapSizeBefore+" headers");
		System.out.println("Before forward had "+paramsMapSizeBefore+" params");
		
		//Call the servlets
		 chain.doFilter(request,response);                               

		headerMap = (Map <String,String>)session.getAttribute(LAST_REQUEST_HEADERS);
		int headerMapSizeAfter = headerMap.size();
		if (headerMapSizeBefore != headerMapSizeAfter) {
			System.err.println("Before forward had "+headerMapSizeBefore+" headers but afterwards had "+headerMapSizeAfter);
		} else {
			System.out.println("Before forward had "+headerMapSizeBefore+" headers and afterwards had "+headerMapSizeAfter);
		}
		 
		params = (Map <String,String[]>)session.getAttribute(LAST_REQUEST_PARAMS);
		int paramsMapSizeAfter = params.size();
		if (paramsMapSizeBefore != paramsMapSizeAfter) {
			System.err.println("Before forward had "+paramsMapSizeBefore+" stored params but afterwards had "+paramsMapSizeAfter);
		} else {
			System.out.println("Before forward had "+paramsMapSizeBefore+" stored params and afterwards had "+paramsMapSizeAfter);
		}

		 //Remove the parameters from the session
		 session.removeAttribute(LAST_REQUEST_PARAMS);
		 session.removeAttribute(LAST_REQUEST_HEADERS);
	}
	

	//Utility method for other classes to get to the stored parameters
	public static Map <String,String[]> getLastRequestParams(HttpSession session) {
		if (session==null)
			return null;
		Map <String,String[]> params = (Map <String,String[]>)session.getAttribute(LAST_REQUEST_PARAMS);
		return params;
	}
	
	//Utility method for other classes to get to the stored headers
	public static Map <String,String> getLastRequestHeaders(HttpSession session) {
		if (session==null)
			return null;
		Map <String,String> params = (Map <String,String>)session.getAttribute(LAST_REQUEST_HEADERS);
		return params;
	}
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// No-op
	}

}
