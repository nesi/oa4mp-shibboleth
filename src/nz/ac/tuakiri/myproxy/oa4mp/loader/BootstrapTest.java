package nz.ac.tuakiri.myproxy.oa4mp.loader;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class BootstrapTest implements ServletContextListener {
    private static String OA4MP_CONFIG_FILE_KEY = "oa4mp:server.config.file";

	public BootstrapTest() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("In class :" + this.getClass().getName() + ".contextInitialized(ServletContextEvent sce)");
		String msg = sce.toString();
		System.out.println("Servlet context event :" + msg);
		ServletContext ctx = sce.getServletContext();
		String pth = ctx.getContextPath();
		System.out.println("Servlet context path :" + pth);
		Enumeration<String> names = ctx.getInitParameterNames();
		if (names.hasMoreElements()) {
			while (names.hasMoreElements()) {
				String param = names.nextElement();
				System.out.println("Context parameter :" + param + "=" + ctx.getInitParameter(param));
			}
		} else {
			System.out.println("NO Context parameters");
		}

		String foundStr = ctx.getInitParameter(OA4MP_CONFIG_FILE_KEY);
		if (foundStr == null) {
			System.out.println("NO entry for OA4MP_CONFIG_FILE_KEY - " + OA4MP_CONFIG_FILE_KEY );
		} else {
			System.out.println("OA4MP_CONFIG_FILE_KEY :" + OA4MP_CONFIG_FILE_KEY + "=" + foundStr);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("In class :" + this.getClass().getName() + ".contextInitialized(ServletContextEvent sce)");
		String msg = sce.toString();
		System.out.println("Servlet context event :" + msg);
		ServletContext ctx = sce.getServletContext();
		String pth = ctx.getContextPath();
		System.out.println("Servlet context path :" + pth);
	}

}
