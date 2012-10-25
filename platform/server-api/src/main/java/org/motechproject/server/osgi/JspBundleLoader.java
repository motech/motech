package org.motechproject.server.osgi;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JspBundleLoader implements BundleLoader, ServletContextAware {

    private static Logger logger = LoggerFactory.getLogger(JspBundleLoader.class);

    private ServletContext servletContext;

    private File tempDir;

    private File destDir;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadBundle(Bundle bundle) throws Exception {
        //we want to build and unpack jar files in application temporary directory
        //if we found jsp file then we will copy it to destination directory
        File tempRoot = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        File destRoot =  new File(servletContext.getRealPath("/"));

        if (tempRoot != null && destRoot != null) {
            tempDir = new File(tempRoot, String.valueOf(bundle.getBundleId()));
            destDir = new File(destRoot, String.valueOf(bundle.getBundleId()));

            //search for jars in bundle
            Enumeration<URL> jars = bundle.findEntries("/", "*.jar", true);
            if (jars != null) {
                while (jars.hasMoreElements()) {

                    URL jarUrl = jars.nextElement();

                    //build jar file
                    File tempJarFile = new File(tempDir, jarUrl.getFile());
                    // There is a problem with creating new directories when loading bundles asynchronously.
                    // This is why this step must be synchronized.
                    synchronized (JspBundleLoader.class) {
                        FileUtils.copyURLToFile(jarUrl, tempJarFile);
                    }

                    JarFile jarFile = new JarFile(tempJarFile);
                    searchForJspFilesInJarFile(jarFile, bundle.getBundleId());

                    tempJarFile.delete();
                }
            }

            tempDir.delete();

            //Search for *.jsp files in bundle
            Enumeration<URL> jsps = bundle.findEntries("/webapp", "*.jsp", true);
            if (jsps != null) {
                while (jsps.hasMoreElements()) {
                    URL jspUrl = jsps.nextElement();

                    File destFile = new File(destDir, jspUrl.getFile());

                    FileUtils.copyURLToFile(jspUrl, destFile);
                    logger.debug("Loaded " + jspUrl.getFile() + " from [" + bundle.getLocation() + "]");
                }
            }

            //Search for *.properties files in bundle
            loadBundleMessageFilesFromBundle(bundle, destRoot, "/webapp/resources/messages");
            loadBundleMessageFilesFromBundle(bundle, destRoot, "/webapp/bundles");
        }
    }

    private void loadBundleMessageFilesFromBundle(final Bundle bundle, final File destRoot, final String pathInBundle) throws Exception {
        Enumeration<URL> messages = bundle.findEntries(pathInBundle, "*.properties", true);
        if (messages != null) {
            File msgDestDir = new File(destRoot, "/WEB-INF/classes/org/motechproject/resources/");
            boolean exists = msgDestDir.exists();

            if (!exists) {
                exists = msgDestDir.mkdirs();
            }

            if (exists) {
                while(messages.hasMoreElements()) {
                    Properties p = new Properties();
                    URL msgUrl = messages.nextElement();
                    String fileName = msgUrl.getFile().substring(msgUrl.getFile().lastIndexOf('/') + 1);
                    int underscore = fileName.indexOf("_");

                    if (underscore != -1) {
                        fileName = "messages" + fileName.substring(underscore);
                    } else {
                        fileName = "messages.properties";
                    }

                    File msgDestFile = new File(msgDestDir, fileName);

                    if (msgDestFile.exists()) {
                        p.load(new FileInputStream(msgDestFile));
                    }

                    p.load(new InputStreamReader(msgUrl.openStream()));
                    logger.debug("Loaded " + msgUrl.getFile() + " from [" + bundle.getLocation() + "]");

                    try (FileOutputStream output = new FileOutputStream(msgDestFile)) {
                        p.store(output, null);
                    }
                }
            }
        }
    }

    private void searchForJspFilesInJarFile(JarFile jarFile, long bundleId) throws Exception {
        Enumeration filesInJar = jarFile.entries();
        while (filesInJar.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) filesInJar.nextElement();
            if (jarEntry != null) {
                if (jarEntry.getName().contains(".jsp")) {
                    InputStream input  = jarFile.getInputStream(jarEntry);
                    try {
                        File tempJspFile = saveJspToTempFile(input);

                        if (tempJspFile != null) {
                            //copy temporary jsp file into right one
                            File destJspFile = new File(destDir, jarEntry.getName());
                            FileUtils.copyURLToFile(tempJspFile.toURI().toURL(), destJspFile);

                            //delete tmp jsp file
                            tempJspFile.delete();
                        }
                    } finally {
                        input.close();
                    }
                }
            }
        }
    }

    //read jsp file from jar and write to temporary file
    //because we cannot use FileOutputStream on web application dir
    private File saveJspToTempFile(InputStream input) throws Exception {
        File tempJspFile = null;

        if (tempDir != null && tempDir.isDirectory()) {
            tempJspFile = new File(tempDir, "temp.jsp");
            tempJspFile.createNewFile();
            FileOutputStream output = new FileOutputStream(tempJspFile);

            try {
                IOUtils.copy(input, output);
            } finally {
                output.close();
            }
        }
            return tempJspFile;
    }
}
