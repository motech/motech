package org.motechproject.mds.repository;

import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.Extent;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The <code>AllFieldTypes</code> repository class allows persistence and retrieving of Field Types
 * in Data Services database.
 */
@Repository
public class AllFieldTypes extends BaseMdsRepository {

    @Transactional
    public AvailableFieldTypeMapping save(AvailableTypeDto type) {
        AvailableFieldTypeMapping fieldTypeMapping = new AvailableFieldTypeMapping(type.getId(), type.getDefaultName(), type.getType());

        return getPersistenceManager().makePersistent(fieldTypeMapping);
    }

    @Transactional
    public List<AvailableTypeDto> getAll() {
        List<AvailableTypeDto> allTypes = new ArrayList<>();
        Extent extent = getPersistenceManager().getExtent(AvailableFieldTypeMapping.class);
        Iterator it = extent.iterator();

        while (it.hasNext()) {
            allTypes.add(((AvailableFieldTypeMapping)it.next()).toDto());
        }

        return allTypes;
    }

    @Transactional
    public boolean typeExists(AvailableTypeDto type) {
        Query query = getPersistenceManager().newQuery(AvailableFieldTypeMapping.class);
        query.setFilter("displayName == name");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        AvailableFieldTypeMapping result = (AvailableFieldTypeMapping) query.execute(type.getType().getDisplayName());

        return result != null;
    }

    @Transactional
    public void delete(String id) {
        Query query = getPersistenceManager().newQuery(AvailableFieldTypeMapping.class);
        query.setFilter("typeId == id");
        query.declareParameters("java.lang.String typeId");
        query.setUnique(true);

        AvailableFieldTypeMapping result = (AvailableFieldTypeMapping) query.execute(id);

        if (result != null) {
            getPersistenceManager().deletePersistent(result);
        }
    }

}
