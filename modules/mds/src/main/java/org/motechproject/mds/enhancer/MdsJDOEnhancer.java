package org.motechproject.mds.enhancer;

import org.datanucleus.api.jdo.JDOEnhancer;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.builder.EntityMetadataBuilder;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jdo.metadata.JDOMetadata;
import java.io.IOException;

/**
 * The <code>MdsJDOEnhancer</code> class is a wrapper for
 * {@link org.datanucleus.api.jdo.JDOEnhancer} class. Its task is to add the missing information
 * into created entity class.
 */
@Component
public class MdsJDOEnhancer extends JDOEnhancer {
    public static final String DATANUCLEUS_PROPERTIES = "datanucleus.properties";

    @Autowired
    public MdsJDOEnhancer(SettingsFacade settingsFacade) {
        super(settingsFacade.getProperties(DATANUCLEUS_PROPERTIES));

        setVerbose(true);
    }

    public byte[] enhance(String className) throws IOException {
        EntityBuilder builder = new EntityBuilder()
                .withClassName(className)
                .withClassLoader(MDSClassLoader.PERSISTANCE);

        builder.build();

        setClassLoader(builder.getClassLoader());

        JDOMetadata metadata = EntityMetadataBuilder.createBaseEntity(
                newMetadata(), className
        );

        registerMetadata(metadata);
        addClass(className, builder.getClassBytes());
        enhance();

        return getEnhancedBytes(className);
    }
}
