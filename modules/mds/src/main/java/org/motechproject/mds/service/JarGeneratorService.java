package org.motechproject.mds.service;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;

/**
 * This interface provides methods to create a bundle jar with all entities defined in MDS module.
 */
public interface JarGeneratorService {

    String MDS_COMMON_CONTEXT = "META-INF/motech/mdsCommonContext.xml";
    String DATANUCLEUS_PROPERTIES = "datanucleus.properties";
    String MOTECH_MDS_PROPERTIES = "motech-mds.properties";
    String MDS_ENTITIES_CONTEXT = "META-INF/motech/mdsEntitiesContext.xml";
    String BLUEPRINT_XML = "META-INF/spring/blueprint.xml";
    String PACKAGE_JDO = "META-INF/package.jdo";
    String BLUEPRINT_TEMPLATE = "/velocity/templates/blueprint-template.vm";
    String MDS_ENTITIES_CONTEXT_TEMPLATE = "/velocity/templates/mdsEntitiesContext-template.vm";
    String BUNDLE_IMPORTS = "bundleImports.txt";

    File generate() throws IOException, NotFoundException, CannotCompileException;

    void regenerateMdsDataBundle(boolean buildDDE);
}
