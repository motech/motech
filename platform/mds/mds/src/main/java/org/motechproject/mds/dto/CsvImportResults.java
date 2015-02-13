package org.motechproject.mds.dto;

import java.io.Serializable;
import java.util.List;

/**
 * This class holds results from a CSV import - IDs of updates and created instances.
 */
public class CsvImportResults implements Serializable {

    private static final long serialVersionUID = 4294495420977348890L;

    private final List<Long> newInstanceIDs;
    private final List<Long> updatedInstanceIDs;
    private final String entityClassName;
    private final String entityName;
    private final String entityModule;
    private final String entityNamespace;

    /**
     * @param entity entity for which this import was performed
     * @param newInstanceIDs a list of IDs for instances that were newly created during import
     * @param updatedInstanceIDs a list of IDs for instances that were updated during import
     */
    public CsvImportResults(EntityDto entity, List<Long> newInstanceIDs, List<Long> updatedInstanceIDs) {
        this.entityClassName = entity.getClassName();
        this.entityName = entity.getName();
        this.entityModule = entity.getModule();
        this.entityNamespace = entity.getNamespace();
        this.newInstanceIDs = newInstanceIDs;
        this.updatedInstanceIDs = updatedInstanceIDs;
    }

    /**
     * @return a list of IDs for instances that were newly created during import
     */
    public List<Long> getNewInstanceIDs() {
        return newInstanceIDs;
    }

    /**
     * @return a list of IDs for instances that were updated during import
     */
    public List<Long> getUpdatedInstanceIDs() {
        return updatedInstanceIDs;
    }

    /**
     * @return the total number of instances that were newly created during import
     */
    public int newInstanceCount() {
        return newInstanceIDs.size();
    }

    /**
     * @return the total number of instances that were updated during import
     */
    public int updatedInstanceCount() {
        return updatedInstanceIDs.size();
    }

    /**
     * @return the name of the entity for which this import was performed
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return the name of the module of the entity for which this import was performed
     */
    public String getEntityModule() {
        return entityModule;
    }

    /**
     * @return the namespace of the entity for which this import was performed
     */
    public String getEntityNamespace() {
        return entityNamespace;
    }

    /**
     * @return the class name of the entity for which this import was performed
     */
    public String getEntityClassName() {
        return entityClassName;
    }

    /**
     * Returns the total number of imported instances. The total number of imported instances
     * is the sum of the number of updated instances and the total number of newly created instances.
     * In other words this is the number of affected instances.
     * @return total number of imported instances
     */
    public int totalNumberOfImportedInstances() {
        return newInstanceCount() + updatedInstanceCount();
    }
}
