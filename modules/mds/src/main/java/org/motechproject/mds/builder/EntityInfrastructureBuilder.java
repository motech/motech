package org.motechproject.mds.builder;

import java.util.List;

/**
 * The <code>EntityInfrastructureBuilder</code> is responsible for building infrastructure for a given entity:
 * repository, interface and service classes.
 */
public interface EntityInfrastructureBuilder {

    List<ClassData> buildInfrastructure(String entityClassName);
}
