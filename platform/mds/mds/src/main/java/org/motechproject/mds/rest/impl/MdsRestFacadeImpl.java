package org.motechproject.mds.rest.impl;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.rest.MdsRestFacade;
import org.motechproject.mds.service.MotechDataService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class MdsRestFacadeImpl<T> implements MdsRestFacade<T> {

    private MotechDataService<T> dataService;
    private AllEntities allEntities;

    private RestOptionsDto restOptions;

    @PostConstruct
    private void init() {
        Class clazz = dataService.getClassType();
        String name = clazz.getName();
        Entity entity = allEntities.retrieveByClassName(name);

        RestOptions restOptsFromDb = entity.getRestOptions();
        restOptions = (restOptsFromDb == null) ? null : restOptsFromDb.toDto();
    }

    @Autowired
    public void setDataService(MotechDataService<T> dataService) {
        this.dataService = dataService;
    }
}
