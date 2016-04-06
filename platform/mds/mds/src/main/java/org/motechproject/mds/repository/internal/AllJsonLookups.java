package org.motechproject.mds.repository.internal;

import org.motechproject.mds.domain.JsonLookup;
import org.motechproject.mds.dto.JsonLookupDto;
import org.motechproject.mds.repository.MotechDataRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AllJsonLookups extends MotechDataRepository<JsonLookup> {

    public AllJsonLookups() {
        super(JsonLookup.class);
    }

    public JsonLookup create(JsonLookupDto dto) {

        JsonLookup entity = new JsonLookup();

        entity.setEntityClassName(dto.getEntityClassName());
        entity.setOriginLookupName(dto.getOriginLookupName());

        return create(entity);
    }

    public JsonLookup getByOriginName(String entityClassName, String originName) {
        return retrieve(new String[] {"entityClassName", "originLookupName"}, new Object[] {entityClassName, originName});
    }
}
