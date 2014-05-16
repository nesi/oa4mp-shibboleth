package nz.ac.tuakiri.myproxy.oa4mp.loader.edu.uiuc.ncsa.myproxy.oa4mp.server.servlet;

import nz.ac.tuakiri.security.servlet.ShibUsernameTransformer;
import edu.uiuc.ncsa.myproxy.oa4mp.loader.edu.uiuc.ncsa.myproxy.oa4mp.server.servlet.AAS2Impl;
import edu.uiuc.ncsa.security.delegation.server.ServiceTransaction;
import edu.uiuc.ncsa.security.delegation.servlet.TransactionState;
import edu.uiuc.ncsa.security.servlet.UsernameTransformer;

@SuppressWarnings("serial")
public class ShibAAS2Impl extends AAS2Impl {

	@SuppressWarnings("unchecked")
	@Override
	public void postprocess(TransactionState state) throws Throwable {
		debug("ShibAAS2Impl:postprocess");
		super.postprocess(state);
		
		UsernameTransformer transformer = getServiceEnvironment().getUsernameTransformer();
		if ((transformer instanceof ShibUsernameTransformer) && ((ShibUsernameTransformer)transformer).isProcessDuringStart()) {
			String username = ((ShibUsernameTransformer)transformer).doIt(state.getRequest());
			((ServiceTransaction)state.getTransaction()).setUsername(username);

			info("ShibAAS2Impl: storing user name = " + username);
			
	        getTransactionStore().save(state.getTransaction());
		}
	}

}
