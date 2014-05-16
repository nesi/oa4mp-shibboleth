package nz.ac.tuakiri.myproxy.oa4mp.loader;

import org.apache.commons.configuration.tree.ConfigurationNode;

import edu.uiuc.ncsa.myproxy.oa4mp.loader.OA4MPBootstrapper;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.util.ConfigurationLoader;

public class ShibOA4MPBootstrapper extends OA4MPBootstrapper {

	@SuppressWarnings("rawtypes")
	@Override
	public ConfigurationLoader getConfigurationLoader(ConfigurationNode node)
			throws MyConfigurationException {
		return new ShibOA4MPConfigurationLoader(node);
	}

}
