<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<% 

	java.util.Map<String, String[]> lastParams 
		= nz.ac.tuakiri.myproxy.oa4mp.loader.RequestIntoSessionFilter.getLastRequestParams(request.getSession());
	int numLastParams = -1;
	if (lastParams != null) {
		numLastParams = lastParams.size();
	}

	java.util.Map<String, String> lastHeaders 
	  = nz.ac.tuakiri.myproxy.oa4mp.loader.RequestIntoSessionFilter.getLastRequestHeaders(request.getSession());
	int numLastHeaders = -1;
	if (lastHeaders != null) {
		numLastHeaders = lastHeaders.size();
	}
	
	String show = request.getParameter("show");

	if ((show != null) && show.equals("1")) {
		response.addHeader("NumberOfStoredParameters", ""+numLastParams);
		response.addHeader("NumberOfStoredHeaders", ""+numLastHeaders);
		response.sendError(200, "Number of stored parameters: " + numLastParams);
		return;
	}
	
	if ((numLastParams < 0) || (numLastHeaders <= 0)) {
		response.sendError(500, "Number of stored parameters: " + numLastParams + "; Number of stored headers: " + numLastHeaders);
		return;
	}
	
	//response.addHeader(name, value)	

	RequestDispatcher rd = request.getRequestDispatcher("authorize-init.jsp");
	rd.forward(request, response);


	if ((show != null) && show.equals("2")) {
		response.addHeader("NumberOfStoredParameters", ""+numLastParams);
		response.addHeader("NumberOfStoredHeaders", ""+numLastHeaders);
	}

%>
