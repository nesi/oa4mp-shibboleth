<%--
  User: Michael Keller
  Date: 13/05/2014
  Properties supplied:
  * clientName = the name of the client
  * clientHome = the home uri of the client
  * AuthUserName = field name containing the user name on submission
  * AuthPassword = field name containing the user's password on submission
  * retryMessage = message displayed if the login in fails.
  * tokenKey = name of hidden field to pass along the authorizationGrant
  * actionToTake = what action that submitting the form invokes.
  * authorizationGrant = the identifier for this transaction
  * action = name of field containing the action the servlet should take
  * actionOk = content of action field in this case telling the service to continue processing.
  * attributeMap = map of shibboleth attributes to be supplied to MyProxy

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>NeSI MyProxy Authorization Page</title>

</head>
<style type="text/css">
.hidden {
	display: none;
}

.unhidden {
	display: table-row;
}

html{font-family:Arial;font-weight:300}
body{margin:0;}

</style>
<body>
         <a class="navbar-brand" href="/" title="Home"><img src="logo.png" alt="Home" width=170/></a>
         
	<h3>NeSI MyProxy Client Authorization Page </h3>
	This allows Tuakiri information to be exchanged with Globus.<br/>
	The Client below is requesting access to your user data.<br/>
               If you approve, please click 'Approve'.
	<c:choose>
		<c:when test="${(userName == '' ||userName == null)}">
               If you approve, please sign in with your username and password.
       </c:when>
		<c:otherwise>
               If you approve, please click 'Approve'.
       </c:otherwise>
	</c:choose>
	<p>
	<form action="${actionToTake}" method="POST">
		<table>
			<tr>
				<td>
					<table border=1>
						<tr>
							<td valign="top">
								<table>
									<tr valign="top">
										<th colspan="2">Client Information</th>
									</tr>
									<tr>
										<td><i>Name:</i></td>
										<td>${clientName}</td>
									</tr>
									<tr>
										<td><i>URL:</i></td>
										<td>${clientHome}</td>
									</tr>
								</table>
							</td>
							<td valign="top">
								<table>
									<tr valign="top">
										<th colspan="2">User Data</th>
									</tr>
									<c:choose>
										<c:when test="${attributeMap != null}">
											<c:forEach items="${attributeMap}" var="parameter">
												<tr>
													<td><i>${parameter.key}</i></td>
													<td>${parameter.value}</td>
												</tr>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<tr>
												<td><i>Username</i></td>
												<td><c:choose>
														<c:when test="${(userName != '' && userName != null)}">
															<input type="text" size="25" name="${AuthUserName}" />
														</c:when>
														<c:otherwise>
                                                               ${userName}
                                                       </c:otherwise>
													</c:choose></td>
											</tr>
											<c:if test="${(userName != '' && userName != null)}">
												<tr>
													<td><i>Password</i></td>
													<td><input type="password" size="25"
														name="${AuthPassword}" /></td>
												</tr>
											</c:if>
										</c:otherwise>
									</c:choose>
								</table>

							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td align="center"><input type="submit"
					value="${(userName == '' || userName== null)?  'Sign In' : 'Approve'}" />
					<a href="${clientHome}" STYLE="TEXT-DECORATION: NONE"><input
						type="button" name="cancel" value="Cancel" /></a></td>
				<td align="center" colspan="2"><b><font color="red">${retryMessage}</font></b></td>
			</tr>
		</table>
		<input type="hidden" id="status" name="${action}" value="${actionOk}" />
		<input type="hidden" id="token" name="${tokenKey}"
			value="${authorizationGrant}" />
	</form>

</body>
</html>
