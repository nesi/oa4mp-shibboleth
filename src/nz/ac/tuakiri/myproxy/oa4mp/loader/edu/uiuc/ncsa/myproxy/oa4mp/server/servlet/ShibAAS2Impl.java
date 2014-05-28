package nz.ac.tuakiri.myproxy.oa4mp.loader.edu.uiuc.ncsa.myproxy.oa4mp.server.servlet;

import javax.security.auth.x500.X500Principal;

import org.globus.gsi.CertUtil;

import nz.ac.tuakiri.security.servlet.ShibUsernameTransformer;
import edu.uiuc.ncsa.myproxy.oa4mp.loader.edu.uiuc.ncsa.myproxy.oa4mp.server.servlet.AAS2Impl;
import edu.uiuc.ncsa.security.delegation.server.ServiceTransaction;
import edu.uiuc.ncsa.security.delegation.servlet.TransactionState;
import edu.uiuc.ncsa.security.delegation.token.MyX509Certificates;
import edu.uiuc.ncsa.security.servlet.UsernameTransformer;

@SuppressWarnings({ "serial", "deprecation" })
public class ShibAAS2Impl extends AAS2Impl {

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
	protected void doRealCertRequest(ServiceTransaction trans,
			String statusString) throws Throwable {
		super.doRealCertRequest(trans, statusString);

		String userName = trans.getUsername();

		MyX509Certificates myCerts = (MyX509Certificates) trans
				.getProtectedAsset();

		if (getTransformer().isReturnDnAsUsername()) {
			if (myCerts.getX509Certificates().length > 0) {
				X500Principal x500Principal = myCerts.getX509Certificates()[0]
						.getSubjectX500Principal();
				userName = CertUtil.toGlobusID(x500Principal);
				debug(statusString + ": USERNAME = " + userName);
			} else {
				userName = "no_certificates_found";
			}
			trans.setUsername(userName);
			info("ShibAAS2Impl.doRealCertRequest: Set username returned to client to first certificate's DN: "
					+ userName);
		}
	}

}
