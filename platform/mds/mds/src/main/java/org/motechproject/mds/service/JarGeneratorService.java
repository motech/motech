package org.motechproject.mds.service;

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

    /**
     * Generates a jar file that contains entity class definitions, repositories, interfaces,
     * implementations of these interfaces. The jar should also contains class related with
     * historical data and trash.
     *
     * @return file that point to an entitites bundle jar.
     * @throws IOException if an I/O error occurs while the jar is creating.
     */
    File generate() throws IOException;

    /**
     * Constructs entities, builds and starts the entities bundle jar
     *
     * @param buildDDE {@code true} if class definitions for entities from outside bundles should
     *                 also be created; otherwise {@code false}.
     * @see #generate()
     */
    void regenerateMdsDataBundle(boolean buildDDE);

    /**
     * Constructs entities, builds the entities bundle jar. The generated bundle will start only if
     * the <strong>startBundle</strong> will be set to {@code true}.
     *
     * @param buildDDE    {@code true} if class definitions for entities from outside bundles should
     *                    also be created; otherwise {@code false}.
     * @param startBundle {@code true} if the generated bundle should start;
     *                    otherwise {@code false}.
     * @see #generate()
     */
    void regenerateMdsDataBundle(boolean buildDDE, boolean startBundle);
}
