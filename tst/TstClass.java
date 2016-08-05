public class TstClass {
	public static void main(String[] args) {
		String classnames[] = {"javax.servlet.ServletContextListener",
				"edu.uiuc.ncsa.security.core.util.ConfigurationLoader",
				"org.apache.commons.configuration.tree.ConfigurationNode",
				"edu.uiuc.ncsa.security.servlet.Bootstrapper",
				"edu.uiuc.ncsa.myproxy.oa4mp.server.servlet.AbstractBootstrapper",
				"edu.uiuc.ncsa.myproxy.oa4mp.oauth2.loader.OA2Bootstrapper",
				"nz.ac.tuakiri.myproxy.oa4mp.loader.ShibOA4MPBootstrapper"};
		for (String classname : classnames) {
			try {
				Class.forName(classname);
				System.out.println("Loaded " + classname);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}