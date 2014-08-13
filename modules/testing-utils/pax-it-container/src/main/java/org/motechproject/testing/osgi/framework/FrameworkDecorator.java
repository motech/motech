package org.motechproject.testing.osgi.framework;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 *  <code>FrameworkDecorator</code> is a base class for framework decorators.
 */
public abstract class FrameworkDecorator implements Framework {

    private Framework framework;

    public FrameworkDecorator(Framework framework) {
        this.framework = framework;
    }

    public Framework getFramework() {
        return framework;
    }

    @Override
    public void init() throws BundleException {
        framework.init();
    }

    @Override
    public FrameworkEvent waitForStop(long timeout) throws InterruptedException {
        return framework.waitForStop(timeout);
    }

    @Override
    public void start() throws BundleException {
        framework.start();
    }

    @Override
    public void start(int options) throws BundleException {
        framework.start(options);
    }

    @Override
    public void stop() throws BundleException {
        framework.stop();
    }

    @Override
    public void stop(int options) throws BundleException {
        framework.stop(options);
    }

    @Override
    public void uninstall() throws BundleException {
        framework.uninstall();
    }

    @Override
    public void update() throws BundleException {
        framework.update();
    }

    @Override
    public void update(InputStream in) throws BundleException {
        framework.update(in);
    }

    @Override
    public long getBundleId() {
        return framework.getBundleId();
    }

    @Override
    public String getLocation() {
        return framework.getLocation();
    }

    @Override
    public String getSymbolicName() {
        return framework.getSymbolicName();
    }

    @Override
    public Enumeration<String> getEntryPaths(String path) {
        return framework.getEntryPaths(path);
    }

    @Override
    public URL getEntry(String path) {
        return framework.getEntry(path);
    }

    @Override
    public Enumeration<URL> findEntries(String path, String filePattern, boolean recurse) {
        return framework.findEntries(path, filePattern, recurse);
    }

    @Override
    public <A> A adapt(Class<A> type) {
        return framework.adapt(type);
    }

    @Override
    public int getState() {
        return framework.getState();
    }

    @Override
    public Dictionary<String, String> getHeaders() {
        return framework.getHeaders();
    }

    @Override
    public ServiceReference<?>[] getRegisteredServices() {
        return framework.getRegisteredServices();
    }

    @Override
    public ServiceReference<?>[] getServicesInUse() {
        return framework.getServicesInUse();
    }

    @Override
    public boolean hasPermission(Object permission) {
        return framework.hasPermission(permission);
    }

    @Override
    public URL getResource(String name) {
        return framework.getResource(name);
    }

    @Override
    public Dictionary<String, String> getHeaders(String locale) {
        return framework.getHeaders(locale);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return framework.loadClass(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return framework.getResources(name);
    }

    @Override
    public long getLastModified() {
        return framework.getLastModified();
    }

    @Override
    public BundleContext getBundleContext() {
        return framework.getBundleContext();
    }

    @Override
    public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(int signersType) {
        return framework.getSignerCertificates(signersType);
    }

    @Override
    public Version getVersion() {
        return framework.getVersion();
    }

    @Override
    public File getDataFile(String filename) {
        return framework.getDataFile(filename);
    }

    @Override
    public int compareTo(Bundle o) {
        return framework.compareTo(o);
    }
}
