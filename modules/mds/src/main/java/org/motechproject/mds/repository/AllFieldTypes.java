package org.motechproject.mds.repository;

import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.springframework.stereotype.Repository;

import javax.jdo.Extent;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>AllFieldTypes</code> repository class allows persistence and retrieving of Field Types
 * in Data Services database.
 */
@Repository
public class AllFieldTypes extends BaseMdsRepository {

    public AvailableFieldTypeMapping save(AvailableTypeDto type) {
        AvailableFieldTypeMapping fieldTypeMapping = new AvailableFieldTypeMapping(type.getId(), type.getDefaultName(), type.getType());

        return getPersistenceManager().makePersistent(fieldTypeMapping);
    }

    public List<AvailableTypeDto> getAll() {
        List<AvailableTypeDto> allTypes = new ArrayList<>();
        Extent extent = getPersistenceManager().getExtent(AvailableFieldTypeMapping.class);

        for (Object anExtent : extent) {
            allTypes.add(((AvailableFieldTypeMapping) anExtent).toDto());
        }

        return allTypes;
    }

    public AvailableFieldTypeMapping getByName(String name) {
        Query query = getPersistenceManager().newQuery(AvailableFieldTypeMapping.class);
        query.setFilter("displayName == name");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        return (AvailableFieldTypeMapping) query.execute(name);
    }

    public boolean typeExists(AvailableTypeDto type) {
        return getByName(type.getType().getDisplayName()) != null;
    }

    public void delete(Long id) {
        Query query = getPersistenceManager().newQuery(AvailableFieldTypeMapping.class);
        query.setFilter("typeId == id");
        query.declareParameters("java.lang.Long typeId");
        query.setUnique(true);

        AvailableFieldTypeMapping result = (AvailableFieldTypeMapping) query.execute(id);

        if (result != null) {
            getPersistenceManager().deletePersistent(result);
        }
    }

    public AvailableFieldTypeMapping getByClassName(String className) {
        Query query = getPersistenceManager().newQuery(AvailableFieldTypeMapping.class);
        query.setFilter("paramTypeClass == typeClass");
        query.declareParameters("java.lang.String paramTypeClass");
        query.setUnique(true);

        return (AvailableFieldTypeMapping) query.execute(className);
    }
}
