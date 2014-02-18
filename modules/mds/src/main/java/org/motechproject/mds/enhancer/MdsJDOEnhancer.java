package org.motechproject.mds.enhancer;

import org.datanucleus.api.jdo.JDOEnhancer;
import org.motechproject.mds.builder.ClassData;

import java.util.Properties;

/**
 * The <code>MdsJDOEnhancer</code> class is a wrapper for
 * {@link org.datanucleus.api.jdo.JDOEnhancer} class. Its task is to add the missing information
 * into created entity class.
 */
public class MdsJDOEnhancer extends JDOEnhancer {

    public MdsJDOEnhancer(Properties config, ClassLoader classLoader) {
        super(config);
        setClassLoader(classLoader);
        setVerbose(true);
    }

    public void addClass(ClassData classData) {
        addClass(classData.getClassName(), classData.getBytecode());
    }
}
