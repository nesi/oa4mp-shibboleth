package nz.ac.tuakiri.myproxy.oa4mp.loader;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.getFirstAttribute;

import java.util.List;

import nz.ac.tuakiri.myproxy.oa4mp.server.ShibOA4MPConfigTags;
import nz.ac.tuakiri.myproxy.oa4mp.server.servlet.ShibUsernameTransformerConfig;
import nz.ac.tuakiri.security.servlet.ShibUsernameTransformer;

import org.apache.commons.configuration.tree.ConfigurationNode;

import edu.uiuc.ncsa.myproxy.oa4mp.loader.OA4MPConfigurationLoader;
import edu.uiuc.ncsa.myproxy.oa4mp.server.ServiceEnvironmentImpl;

@SuppressWarnings("serial")
public class ShibOA4MPConfigurationLoader<T extends ServiceEnvironmentImpl>
		extends OA4MPConfigurationLoader<T> {

	private ShibUsernameTransformerConfig shibUsernameTransformerConfig;

	public ShibOA4MPConfigurationLoader(ConfigurationNode node) {
		super(node);
	}

	@Override
	public T createInstance() {
		T t = super.createInstance();
		ShibUsernameTransformer usernameTransformer = new ShibUsernameTransformer(
				getShibUsernameTransformerConfig(), t.getMyLogger());
		t.setUsernameTransformer(usernameTransformer);
		return t;
	}

	public ShibUsernameTransformerConfig getShibUsernameTransformerConfig() {
		if (shibUsernameTransformerConfig == null) {
			List<?> kids = cn
					.getChildren(ShibOA4MPConfigTags.SHIBBOLETH_USERNAME_TRANSFORMER);

			String attributesToSend = null;
			boolean myproxyWorkaround = false;
			boolean requireHeader = false;
			boolean processDuringStart = false;
			if (!kids.isEmpty()) {
				ConfigurationNode sn = (ConfigurationNode) kids.get(0);
				try {
					attributesToSend = getFirstAttribute(
							sn,
							ShibOA4MPConfigTags.SHIBBOLETH_USERNAME_TRANSFORMER_ATTRIBUTES_TO_SEND);
					debug("Read attributesToSend: " + attributesToSend);
					myproxyWorkaround = Boolean
							.parseBoolean(getFirstAttribute(
									sn,
									ShibOA4MPConfigTags.SHIBBOLETH_USERNAME_TRANSFORMER_MYPROXY_WORKAROUND));
					debug("Read myproxyWorkaround: " + myproxyWorkaround);
					requireHeader = Boolean
							.parseBoolean(getFirstAttribute(
									sn,
									ShibOA4MPConfigTags.SHIBBOLETH_USERNAME_TRANSFORMER_REQUIRE_HEADER));
					debug("Read requireHeader: " + requireHeader);
					processDuringStart = Boolean
							.parseBoolean(getFirstAttribute(
									sn,
									ShibOA4MPConfigTags.SHIBBOLETH_USERNAME_TRANSFORMER_PROCESS_DURING_START));
					debug("Read processDuringStart: " + processDuringStart);
					processDuringStart = Boolean
							.parseBoolean(getFirstAttribute(
									sn,
									ShibOA4MPConfigTags.SHIBBOLETH_USERNAME_TRANSFORMER_PROCESS_DURING_START));
					debug("Read processDuringStart: " + processDuringStart);
				} catch (Throwable t) {
					info("Error loading authorization configuration. Disabling use of headers");
				}
			}
			shibUsernameTransformerConfig = new ShibUsernameTransformerConfig(
					attributesToSend, myproxyWorkaround, requireHeader, processDuringStart);
		}
		return shibUsernameTransformerConfig;
	}

}
