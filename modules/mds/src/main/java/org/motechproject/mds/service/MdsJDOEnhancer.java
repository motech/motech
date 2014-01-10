package org.motechproject.mds.service;

import org.datanucleus.api.jdo.JDOEnhancer;
import org.motechproject.mds.factory.EntityMetadataFactory;
import org.motechproject.server.config.SettingsFacade;

import javax.jdo.metadata.JDOMetadata;
import java.io.IOException;

/**
 * The <code>MdsJDOEnhancer</code> class is a wrapper for
 * {@link org.datanucleus.api.jdo.JDOEnhancer} class.
 */
public class MdsJDOEnhancer extends JDOEnhancer {
    public static final String DATANUCLEUS_PROPERTIES = "datanucleus.properties";

    public MdsJDOEnhancer(SettingsFacade settingsFacade) {
        super(settingsFacade.getProperties(DATANUCLEUS_PROPERTIES));

        setVerbose(true);
    }

    public byte[] enhance(EntityBuilder builder) throws IOException {
        builder.build();

        setClassLoader(builder.getClassLoader());

        JDOMetadata metadata = EntityMetadataFactory.createBaseEntity(
                newMetadata(), builder.getClassName()
        );

        registerMetadata(metadata);
        addClass(builder.getClassName(), builder.getClassBytes());
        enhance();

        return getEnhancedBytes(builder.getClassName());
    }
}
