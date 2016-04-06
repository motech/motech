package org.motechproject.mds.service.impl;

import org.motechproject.mds.dto.JsonLookupDto;
import org.motechproject.mds.repository.internal.AllJsonLookups;
import org.motechproject.mds.service.JsonLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonLookupServiceImpl implements JsonLookupService {

    private AllJsonLookups allJsonLookups;

    @Override
    public void createJsonLookup(JsonLookupDto jsonLookup) {
        allJsonLookups.create(jsonLookup);
    }

    @Override
    public boolean exists(String entityClassName, String originLookupName) {
        return allJsonLookups.getByOriginName(entityClassName, originLookupName) != null;
    }

    @Autowired
    public void setAllJsonLookups(AllJsonLookups allJsonLookups) {
        this.allJsonLookups = allJsonLookups;
    }
}
