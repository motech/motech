package org.motechproject.mds.enhancer;

import org.apache.commons.io.input.ReaderInputStream;
import org.datanucleus.api.jdo.JDOEnhancer;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * The <code>MdsJDOEnhancer</code> class is a wrapper for
 * {@link org.datanucleus.api.jdo.JDOEnhancer} class. Its task is to add the missing information
 * into created entity class.
 */
public class MdsJDOEnhancer extends JDOEnhancer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MdsJDOEnhancer.class);

    public MdsJDOEnhancer(Properties config, ClassLoader classLoader) {
        super(config);
        setClassLoader(classLoader);
        setVerbose(true);
    }

    private Map<String,StringBuilder> mp = null;
    public void logEntity(String cn, String message) {
        if(mp == null)
            mp = new HashMap<String, StringBuilder>();
        if(!mp.containsKey(cn))
            mp.put(cn,new StringBuilder());
        StringBuilder sb = mp.get(cn);
        sb.append(message);
        sb.append('\n');
    }

    public void dump() {
        if(mp == null) return;
        //String[] rgkey = (String[])mp.keySet().toArray();
        for(String key : mp.keySet()) {
            StringBuilder sb = mp.get(key);
            for(String st1 : sb.toString().split("\n"))
                LOGGER.debug(key+":"+st1);
        }
    }


    public void addClass(ClassData classData) {
        logEntity(classData.getClassName(), "addClass");
        addClass(classData.getClassName(), classData.getBytecode());
    }
}
