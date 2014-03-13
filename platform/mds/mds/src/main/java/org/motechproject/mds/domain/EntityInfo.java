package org.motechproject.mds.domain;

import org.motechproject.mds.util.ClassName;

/**
 * The <code>EntityInfo</code> class contains base information about the given entity like class
 * name or infrastructure classes name.
 *
 * @see org.motechproject.mds.service.JarGeneratorService
 */
public class EntityInfo {
    private String className;
    private String repository;
    private String interfaceName;
    private String serviceName;

    public String getName() {
        return ClassName.getSimpleName(className);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String[] getInfrastructure() {
        return new String[]{repository, interfaceName, serviceName};
    }
}
