package org.motechproject.mds.builder;

import org.motechproject.mds.domain.EntityMapping;

import javax.jdo.metadata.JDOMetadata;

/**
 * The <code>EntityMetadataBuilderImpl</code> class is responsible for building jdo metadata for an
 * entity class.
 */
public interface EntityMetadataBuilder {

    /**
     * Add to the empty {@link javax.jdo.metadata.JDOMetadata} information about package and
     * class name.
     *
     * @param md      a empty instance of {@link javax.jdo.metadata.JDOMetadata}.
     * @param mapping a instance of {@link org.motechproject.mds.domain.EntityMapping}
     * @return an instance of {@link javax.jdo.metadata.JDOMetadata} with information about package
     * and class name.
     */
    JDOMetadata createBaseEntity(JDOMetadata md, EntityMapping mapping);
}
