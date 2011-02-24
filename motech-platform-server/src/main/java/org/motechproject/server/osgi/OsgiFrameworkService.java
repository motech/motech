package org.motechproject.server.osgi;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

/**
 * 
 * @author Ricky Wang
 */
public class OsgiFrameworkService implements ApplicationContextAware {

	private static Logger logger = LoggerFactory.getLogger(OsgiFrameworkService.class);
	
	private ApplicationContext applicationContext;
	
	private String bundleFolder;

	@Autowired
	private Framework osgiFramework;
	
	private List<BundleLoader> bundleLoaders;

	/**
	 * Initialize and start the OSGi framework
	 */
	public void start() {
		try {
			ServletContext servletContext = ((WebApplicationContext)applicationContext).getServletContext();
			
			osgiFramework.init();
			
			BundleContext bundleContext = osgiFramework.getBundleContext();
			
			//This is mandatory for Felix http servlet bridge
			servletContext.setAttribute(BundleContext.class.getName(), bundleContext);

			//install bundles
			ArrayList<Bundle> bundles = new ArrayList<Bundle>();
			for (URL url : findBundles(servletContext)) {
				logger.debug("Installing bundle [" + url + "]");
				Bundle bundle = bundleContext.installBundle(url.toExternalForm());								
				bundles.add(bundle);
			}

			for (Bundle bundle : bundles) {
				//custom bundle loaders 
				if (bundleLoaders != null) {
					for (BundleLoader loader : bundleLoaders) {
						loader.loadBundle(bundle);
					}
				}
				bundle.start();
			}
			
			osgiFramework.start();
			logger.info("OSGi framework started");
		} catch (Throwable e) {
			logger.error("Failed to start OSGi framework", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Stop the OSGi framework.
	 */
	public void stop() {
		try {			
			if (osgiFramework != null) {
				osgiFramework.stop();
				logger.info("OSGi framework stopped");
			}
		} catch (Throwable e) {
			logger.error("Error stopping OSGi framework", e);
			throw new RuntimeException(e);
		}
	}

	private List<URL> findBundles(ServletContext servletContext) throws Exception {
		List<URL> list = new ArrayList<URL>();
		@SuppressWarnings("unchecked")
		Set<String> paths = servletContext.getResourcePaths(bundleFolder);
		if (paths != null) {
			for (String path : paths) {
				if (path.endsWith(".jar")) {
					URL url = servletContext.getResource(path);
					if (url != null) {
						list.add(url);
					}
				}
			}
		}
		return list;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		applicationContext = ctx;
	}

	public void setBundleFolder(String bundleFolder) {
		this.bundleFolder = bundleFolder;
	}

	public void setOsgiFramework(Framework osgiFramework) {
		this.osgiFramework = osgiFramework;
	}	

	public void setBundleLoaders(List<BundleLoader> bundleLoaders) {
		this.bundleLoaders = bundleLoaders;
	}	

}