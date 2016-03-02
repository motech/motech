package org.motechproject.mds.entityinfo;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.motechproject.mds.exception.entity.EntityNotFoundException;
import org.motechproject.mds.util.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link EntityInfoReader} which reads entity information from json
 * files from the META-INF/entity-info directory inside the classpath. Used by mds-entities bundle,
 * which has all entity schema packed inside of it.
 */
public class EntityInfoReaderImpl implements EntityInfoReader {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<Long, String> idMapping = new HashMap<>();

    public EntityInfoReaderImpl(Map<Long, String> idMapping) {
        this.idMapping = idMapping;
    }

    @Autowired
    private BundleContext bundleContext;

    @Override
    public EntityInfo getEntityInfo(String entityClassName) {
        String file = "META-INF/entity-info/" + entityClassName + ".json";

        // the file is inside the entities bundle
        ClassLoader entitiesCl = getMdsEntitiesBundleClassLoader();

        try (InputStream in = entitiesCl.getResourceAsStream(file)) {
            return objectMapper.readValue(in, EntityInfo.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read entity info for " + entityClassName, e);
        }
    }

    public EntityInfo getEntityInfo(Long entityId) {
        String entityClassName = idMapping.get(entityId);

        if (StringUtils.isNotBlank(entityClassName)) {
            return getEntityInfo(entityClassName);
        } else {
            throw new EntityNotFoundException(entityId);
        }
    }

    public Collection<String> getEntitiesClassNames() {
        return idMapping.values();
    }

    // TODO: MOTECH-1466 - use util/helper here after redoing MDS package structure
    private ClassLoader getMdsEntitiesBundleClassLoader() {
        Bundle bundle =  OsgiBundleUtils.findBundleBySymbolicName(bundleContext, Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
        return bundle.adapt(BundleWiring.class).getClassLoader();
    }
}
