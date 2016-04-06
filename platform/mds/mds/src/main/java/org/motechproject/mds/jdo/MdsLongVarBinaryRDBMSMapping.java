package org.motechproject.mds.jdo;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.datastore.LongVarBinaryRDBMSMapping;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.util.NucleusLogger;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.motechproject.mds.helper.bundle.MdsBundleHelper;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;

import java.util.Map;

/**
 * Mapping of a LONGVARBINARY RDBMS type. This implementation will try use context class loader, joda -time bundle class
 * loader and mds-entities
 * bundle class loader for resolving classes during object deserialisation when errors occur when using
 * the default implementation.
 */
public class MdsLongVarBinaryRDBMSMapping extends LongVarBinaryRDBMSMapping {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsLongVarBinaryRDBMSMapping.class);

    public MdsLongVarBinaryRDBMSMapping(JavaTypeMapping mapping, RDBMSStoreManager storeMgr, Column col) {
        super(mapping, storeMgr, col);
    }

    @Override
    protected Object getObjectForBytes(byte[] bytes, int param) {
        if (getJavaTypeMapping().isSerialised() && getJavaTypeMapping().getType().equals(Map.class.getName())) {
            return deserialize(bytes);
        }

        try {
            return super.getObjectForBytes(bytes, param);
        } catch (Exception e) {
            return deserialize(bytes);
        }
    }

    private Object deserialize(byte[] bytes) {
        BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        Bundle mdsEntitiesBundle = MdsBundleHelper.findMdsBundle(bundleContext);
        Bundle jodaTimeBundle = OsgiBundleUtils.findBundleBySymbolicName(bundleContext, "joda-time");

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new MdsObjectInputStream(bais, mdsEntitiesBundle, jodaTimeBundle)){
            return ois.readObject();
        } catch (StreamCorruptedException e) {
            String msg = "StreamCorruptedException: object is corrupted";
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusUserException(msg, e).setFatal();
        } catch (IOException e) {
            String msg = "IOException: error when reading object";
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusUserException(msg, e).setFatal();
        } catch (ClassNotFoundException e) {
            String msg = "ClassNotFoundException: error when creating object";
            NucleusLogger.DATASTORE.error(msg);
            throw new NucleusUserException(msg, e).setFatal();
        }
    }

    private class MdsObjectInputStream extends ObjectInputStream {

        private Bundle mdsEntietiesBundle;
        private Bundle jodaTimeBundle;

        protected MdsObjectInputStream() throws IOException, SecurityException {
        }

        public MdsObjectInputStream(InputStream in, Bundle mdsEntitiesBundle, Bundle jodaTimeBundle) throws IOException {
            super(in);
            this.jodaTimeBundle = jodaTimeBundle;
            this.mdsEntietiesBundle = mdsEntitiesBundle;
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            try {
                if (desc.getName().startsWith("org.joda.time")) {
                    return jodaTimeBundle.loadClass(desc.getName());
                }
            } catch (ClassNotFoundException e) {
                LOGGER.debug("Class {} not found in joda-time bundle", desc.getName());
            }

            try {
                LOGGER.debug("Loading {} class with context class loader", desc.getName());
                return Thread.currentThread().getContextClassLoader().loadClass(desc.getName());
            } catch (ClassNotFoundException e) {
                try {
                    LOGGER.debug("Loading {} class with class loader from entities bundle");
                    return mdsEntietiesBundle.loadClass(desc.getName());
                } catch (ClassNotFoundException ex) {
                    return super.resolveClass(desc);
                }
            }
        }
    }
}
