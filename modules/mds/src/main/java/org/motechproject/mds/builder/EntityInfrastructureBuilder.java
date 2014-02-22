package org.motechproject.mds.builder;

import org.motechproject.mds.domain.Entity;

import java.util.List;

/**
 * The <code>EntityInfrastructureBuilder</code> is responsible for building infrastructure for a given entity:
 * repository, interface and service classes.
 */
public interface EntityInfrastructureBuilder {

    List<ClassData> buildInfrastructure(Entity entity);
}
