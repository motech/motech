package org.motechproject.mds.builder;

import org.motechproject.mds.domain.Entity;

import javax.jdo.metadata.JDOMetadata;

/**
 * The <code>EntityMetadataBuilderImpl</code> class is responsible for building jdo metadata for an
 * entity class.
 */
public interface EntityMetadataBuilder {

    /**
     * Adds information about package and class name to a {@link javax.jdo.metadata.JDOMetadata}
     * instance.
     *
     * @param jdoMetadata a empty instance of {@link javax.jdo.metadata.JDOMetadata}.
     * @param entity      a instance of {@link org.motechproject.mds.domain.Entity}
     */
    void addEntityMetadata(JDOMetadata jdoMetadata, Entity entity);

    /**
     * Adds base information about package and class name to a
     * {@link javax.jdo.metadata.JDOMetadata} instance.
     *
     * @param jdoMetadata a empty instance of {@link javax.jdo.metadata.JDOMetadata}.
     * @param classData   a instance of {@link org.motechproject.mds.builder.ClassData}
     */
    void addBaseMetadata(JDOMetadata jdoMetadata, ClassData classData);
}
