package nz.ac.tuakiri.myproxy.oa4mp.loader.edu.uiuc.ncsa.myproxy.oa4mp.server.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import nz.ac.tuakiri.security.servlet.ShibUsernameTransformer;
import edu.uiuc.ncsa.myproxy.oa4mp.oauth2.servlet.OA2AuthorizationServer;
//import edu.uiuc.ncsa.myproxy.oa4mp.servlet.AAS2Impl;
import edu.uiuc.ncsa.myproxy.oa4mp.server.servlet.MyProxyDelegationServlet;
import edu.uiuc.ncsa.security.delegation.server.ServiceTransaction;
import edu.uiuc.ncsa.security.delegation.servlet.TransactionState;
import edu.uiuc.ncsa.security.servlet.UsernameTransformer;

@SuppressWarnings("serial")
public class ShibAAS2Impl extends OA2AuthorizationServer /*AbstractAuthorizationServletImpl*/ /*AAS2Impl*/ {

	private ShibUsernameTransformer transformer;

	private ShibUsernameTransformer getTransformer() {
		if (transformer == null) {
			UsernameTransformer usernameTransformer = getServiceEnvironment()
					.getUsernameTransformer();
			if (usernameTransformer instanceof ShibUsernameTransformer) {
				transformer = (ShibUsernameTransformer) usernameTransformer;
			}
		}
		return transformer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void postprocess(TransactionState state) throws Throwable {
		debug("ShibAAS2Impl:postprocess");
		super.postprocess(state);

		if ((getTransformer() != null) && getTransformer().isProcessDuringStart()) {
			String username = getTransformer().createUsername(state.getRequest());
			((ServiceTransaction) state.getTransaction()).setUsername(username);

			info("ShibAAS2Impl: storing user name = " + username);

			getTransactionStore().save(state.getTransaction());
		}
	}

	@Override
    protected void createRedirect(HttpServletRequest request, HttpServletResponse response, ServiceTransaction trans) throws Throwable {
		debug("ShibAAS2Impl:createRedirect");
		if (getTransformer() != null) {
			String userName = trans.getUsername();
			info("3.b. transaction has user name = " + userName);
			// The right place to invoke the pre-processor.
			preprocess(new TransactionState(request, response, null, trans));
			String statusString = " transaction =" + trans.getIdentifierString() + " and client=" + trans.getClient().getIdentifierString();
			trans.setVerifier(MyProxyDelegationServlet.getServiceEnvironment().getTokenForge().getVerifier());
			MyProxyDelegationServlet.getServiceEnvironment().getTransactionStore().save(trans);

			createMPConnection(trans.getIdentifier(), userName, null, trans.getLifetime(), statusString);
			doRealCertRequest(trans, statusString);
			debug("4.a. verifier = " + trans.getVerifier() + ", " + statusString);
			String cb = createCallback(trans, getFirstParameters(request));
			info("4.a. starting redirect to " + cb + ", " + statusString);
			response.sendRedirect(cb);
			info("4.b. Redirect to callback " + cb + " ok, " + statusString);
		} else {
			super.createRedirect(request, response, trans);
		}
    }
}
