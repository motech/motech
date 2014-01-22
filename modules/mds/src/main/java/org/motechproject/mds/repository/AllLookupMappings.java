package org.motechproject.mds.repository;

import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.LookupMapping;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.ex.LookupNotFoundException;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;

/**
 * The <code>AllLookupMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.LookupMapping}.
 */
@Repository
public class AllLookupMappings extends BaseMdsRepository {

    public LookupMapping save(LookupDto lookup, EntityMapping entity) {
        LookupMapping mapping = new LookupMapping(lookup.getLookupName(), lookup.isSingleObjectReturn(), entity);

        return getPersistenceManager().makePersistent(mapping);
    }

    public LookupMapping update(LookupDto lookup) {
        LookupMapping mapping = getLookupById(lookup.getId());

        if (mapping == null) {
            throw new LookupNotFoundException();
        }

        mapping.setLookupName(lookup.getLookupName());
        mapping.setSingleObjectReturn(lookup.isSingleObjectReturn());

        return getPersistenceManager().makePersistent(mapping);
    }

    public void remove(LookupDto lookup) {
        LookupMapping lookupMapping = getLookupById(lookup.getId());

        if (lookupMapping != null) {
            getPersistenceManager().deletePersistent(lookupMapping);
        }
    }

    public LookupMapping getLookupById(Long id) {
        Query query = getPersistenceManager().newQuery(LookupMapping.class);
        query.setFilter("lookupId == id");
        query.declareParameters("java.lang.Long lookupId");
        query.setUnique(true);

        return (LookupMapping) query.execute(id);
    }
}
