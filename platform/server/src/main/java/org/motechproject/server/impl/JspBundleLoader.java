package org.motechproject.server.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.api.BundleLoader;
import org.motechproject.server.api.BundleLoadingException;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
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
    public void loadBundle(Bundle bundle) throws BundleLoadingException {
        //we want to build and unpack jar files in application temporary directory
        //if we found jsp file then we will copy it to destination directory
        File tempRoot = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        File destRoot = new File(servletContext.getRealPath("/"));
        try {
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
                        searchForJspFilesInJarFile(jarFile);

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
                for (String path : Arrays.asList("/webapp/resources/messages", "/webapp/bundles", "/webapp/messages")) {
                    loadBundleMessageFilesFromBundle(bundle, destRoot, path);
                }
            }
        } catch (Exception e) {
            throw new BundleLoadingException(e);
        }
    }

    private void loadBundleMessageFilesFromBundle(final Bundle bundle, final File destRoot, final String pathInBundle)
            throws IOException {
        Enumeration<URL> messages = bundle.findEntries(pathInBundle, "*.properties", true);
        if (messages != null) {
            File msgDestDir = new File(destRoot, "/WEB-INF/classes/org/motechproject/resources/");
            boolean exists = msgDestDir.exists();

            if (!exists) {
                exists = msgDestDir.mkdirs();
            }

            if (exists) {
                while (messages.hasMoreElements()) {
                    Properties p = new Properties();
                    URL msgUrl = messages.nextElement();
                    String fileName = msgUrl.getFile().substring(msgUrl.getFile().lastIndexOf('/') + 1);
                    int underscore = fileName.indexOf('_');

                    if (underscore != -1) {
                        fileName = "messages" + fileName.substring(underscore);
                    } else {
                        fileName = "messages.properties";
                    }

                    File msgDestFile = new File(msgDestDir, fileName);

                    if (msgDestFile.exists()) {
                        try (FileInputStream fileInputStream = new FileInputStream((msgDestFile))) {
                            p.load(fileInputStream);
                        }
                    }
                    try (InputStreamReader inputStreamReader = new InputStreamReader(msgUrl.openStream())) {
                        p.load(inputStreamReader);
                        logger.debug("Loaded " + msgUrl.getFile() + " from [" + bundle.getLocation() + "]");
                    }

                    try (FileOutputStream output = new FileOutputStream(msgDestFile)) {
                        p.store(output, null);
                        reloadBundles();
                    }
                }
            }
        }
    }

    private void searchForJspFilesInJarFile(JarFile jarFile) throws IOException {
        Enumeration filesInJar = jarFile.entries();
        while (filesInJar.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) filesInJar.nextElement();
            if (jarEntry != null) {
                if (jarEntry.getName().contains(".jsp")) {
                    InputStream input = jarFile.getInputStream(jarEntry);
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
                        IOUtils.closeQuietly(input);
                    }
                }
            }
        }
    }

    //read jsp file from jar and write to temporary file
    //because we cannot use FileOutputStream on web application dir
    private File saveJspToTempFile(InputStream input) throws IOException {
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

    public static void reloadBundles() {
        try {
            clearMap(ResourceBundle.class, null, "cacheList");
            clearTomcatCache();
        } catch (Exception e) {
            logger.error("Could not reload resource bundles"+e.getMessage());
        }
    }


    private static void clearTomcatCache() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class cl = loader.getClass();

        try {
            if ("org.apache.catalina.loader.WebappClassLoader".equals(cl.getName())) {
                clearMap(cl, loader, "resourceEntries");
            } else {
                logger.error("class loader " + cl.getName() + " is not tomcat loader.");
            }
        } catch (Exception e) {
            logger.error("couldn't clear tomcat cache"+e.getMessage());
        }
    }


    private static void clearMap(Class cl, Object obj, String name) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Field field = cl.getDeclaredField(name);
        field.setAccessible(true);
        Object cache = field.get(obj);
        Class ccl = cache.getClass();
        Method clearMethod = ccl.getMethod("clear", null);
        clearMethod.invoke(cache, null);
    }
}
