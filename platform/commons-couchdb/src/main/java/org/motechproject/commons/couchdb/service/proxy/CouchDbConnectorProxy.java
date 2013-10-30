package org.motechproject.commons.couchdb.service.proxy;

import org.ektorp.CouchDbConnector;
import org.motechproject.commons.couchdb.service.impl.CouchDbManagerImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class CouchDbConnectorProxy implements InvocationHandler {

    private CouchDbManagerImpl couchDbManager;
    private String dbName;
    private CouchDbConnector targetConnector;

    public static CouchDbConnector newInstance(CouchDbManagerImpl couchDbManager, String dbName) {
        return (CouchDbConnector) java.lang.reflect.Proxy.newProxyInstance(CouchDbConnector.class.getClassLoader(),
                new Class<?>[]{CouchDbConnector.class}, new CouchDbConnectorProxy(couchDbManager, dbName));
    }

    private CouchDbConnectorProxy(CouchDbManagerImpl couchDbManager, String dbName) {
        this.couchDbManager = couchDbManager;
        this.dbName = dbName;
    }

    public Object invoke(Object proxy, Method m, Object[] args) throws IllegalAccessException, InvocationTargetException {
        Object result;
        try {
            if (targetConnector == null) {
                targetConnector = couchDbManager.getTargetConnector(dbName);
            }
            result = m.invoke(targetConnector, args);

        } catch (InvocationTargetException e) {
            throw e;
        }
        return result;
    }
}
