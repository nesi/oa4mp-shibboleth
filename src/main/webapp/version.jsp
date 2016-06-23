<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>OA4MP</title>
</head>
<body>
  <p><pre>
  Project Maven group    is <i>GROUPID</i></body>
  Project Maven artifact is <i>ARTIFACTID</i></body>
  Project Maven version  is <i>PROJECT_VERSION</i>
  </pre></p>

  <%@ page import = "java.util.*" %>

  <h1>HTTP Request Headers Received</h1>
  <table border="1" cellpadding="4" cellspacing="0">
  <%
     java.util.Enumeration eNames = request.getHeaderNames();
     while (eNames.hasMoreElements()) {
        String name = (String) eNames.nextElement();
        String value = normalize(request.getHeader(name));
  %>
     <tr><td><%= name %></td><td><%= value %></td></tr>
  <%
     }
  %>
  </table>

  <h1>HTTP Request Parameters Received</h1>
  <table border="1" cellpadding="4" cellspacing="0">
  <%
      java.util.Map<String, String[]> parameters = request.getParameterMap();
      for(String parameter : parameters.keySet()) {
          String[] values = parameters.get(parameter);
          String value="";
          if (values != null) {
            if (values.length == 1) {
              value = values[0];
            } else for(String item : values) {
              value = value + item + ",";
            }
          }
          %>
             <tr><td><%= parameter %></td><td><%= value %></td></tr>
          <%
      }
      %>
  </table>

</body>
</html>

<%!
   private String normalize(String value)
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < value.length(); i++) {
         char c = value.charAt(i);
         sb.append(c);
         if (c == ';')
            sb.append("<br>");
      }
      return sb.toString();
   }
%>
