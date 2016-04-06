package org.motechproject.mds.json;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.ImportExportBlueprint;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.helper.DataServiceHelper;
import org.motechproject.mds.helper.EntityHelper;
import org.motechproject.mds.helper.RelationshipResolver;
import org.motechproject.mds.repository.internal.AllEntities;
import org.motechproject.mds.repository.internal.AllTypes;
import org.motechproject.mds.service.MotechDataService;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The <code>ImportContext</code> class holds all data needed in import process. It also provides methods
 * that must be executed in a proper context.
 *
 * @see org.motechproject.mds.domain.ImportExportBlueprint
 */
public class ImportContext {

    private ImportExportBlueprint blueprint;
    private BundleContext bundleContext;
    private AllEntities allEntities;
    private AllTypes allTypes;
    private RelationshipResolver relationshipResolver;
    private List<Entity> resolvedEntities;
    private List<Entity> unresolvedEntities;
    private Set<String> affectedModules;
    private Map<String, Map<Long, Object>> importedInstances;

    public ImportContext(ImportExportBlueprint blueprint, BundleContext bundleContext,
                         AllEntities allEntities, AllTypes allTypes, RelationshipResolver relationshipResolver) {
        this.blueprint = blueprint;
        this.bundleContext = bundleContext;
        this.allEntities = allEntities;
        this.allTypes = allTypes;
        this.relationshipResolver = relationshipResolver;
        this.resolvedEntities = new ArrayList<>();
        this.unresolvedEntities = new ArrayList<>();
        this.affectedModules = new HashSet<>();
        this.importedInstances = new HashMap<>();
    }

    public Entity setupNewEntity(String entityName) {
        Entity entity = new Entity(entityName);
        EntityHelper.addDefaultFields(entity, allTypes);
        return entity;
    }

    public Entity setupExistingEntity(Entity existingEntity) {
        EntityHelper.removeAdditionalFieldsAndLookups(existingEntity);
        return existingEntity;
    }

    public Entity getEntity(String entityClassName) {
        return allEntities.retrieveByClassName(entityClassName);
    }

    public Type getType(String typeName) {
        return allTypes.retrieveByDisplayName(typeName);
    }

    public MotechDataService getDataService(String entityClassName) {
        return DataServiceHelper.getDataService(bundleContext, entityClassName);
    }

    public ImportExportBlueprint getBlueprint() {
        return blueprint;
    }

    public void setEntities(Set<Entity> entities) {
        resolvedEntities = relationshipResolver.removeUnresolvedEntities(entities);
        unresolvedEntities = new ArrayList<>(entities);
        unresolvedEntities.removeAll(resolvedEntities);
    }

    private void persistEntity(Entity entity) {
        if (null == entity.getId()) {
            allEntities.create(entity);
        } else {
            allEntities.updateAndIncrementVersion(entity);
        }
    }

    public void persistResolvedEntities() {
        for (Entity entity : resolvedEntities) {
            persistEntity(entity);
            if (StringUtils.isNotBlank(entity.getModule())) {
                affectedModules.add(entity.getModule());
            }
        }
    }

    public boolean hasUnresolvedEntities() {
        return unresolvedEntities.size() != 0;
    }

    public List<Entity> getUnresolvedEntities() {
        return unresolvedEntities;
    }

    public Set<String> getAffectedModules() {
        return affectedModules;
    }

    public String[] getAffectedModulesArray() {
        return affectedModules.toArray(new String[affectedModules.size()]);
    }

    public Object getInstanceOfEntity(String entityClassName, Long refId) {
        Map<Long, Object> entityInstances = importedInstances.get(entityClassName);
        if (null != entityInstances) {
            return entityInstances.get(refId);
        } else {
            return null;
        }
    }

    public Collection<Object> getInstancesOfEntity(String entityClassName, List<Long> refIds) {
        List<Object> instances = new ArrayList<>(refIds.size());
        for (Long refId : refIds) {
            Object instance = getInstanceOfEntity(entityClassName, refId);
            if (null != instance) {
                instances.add(instance);
            }
        }
        return instances;
    }

    public void putInstanceOfEntity(String entityClassName, long refId, Object instance) {
        Map<Long, Object> importedEntityInstances = importedInstances.get(entityClassName);
        if (null == importedEntityInstances) {
            importedEntityInstances = new HashMap<>();
            importedInstances.put(entityClassName, importedEntityInstances);
        }
        importedEntityInstances.put(refId, instance);
    }

    public void removeExistingInstances() {
        for (Entity entity : resolvedEntities) {
            getDataService(entity.getClassName()).deleteAll();
        }
    }
}
