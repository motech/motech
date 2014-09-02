package org.motechproject.mds.rest;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.ex.rest.RestOperationNotSupportedException;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.MotechDataService;

import javax.annotation.PostConstruct;
import java.util.List;

public class MdsRestFacadeImpl<T> implements MdsRestFacade<T> {

    private MotechDataService<T> dataService;
    private AllEntities allEntities;
    private String entityClassName;

    private RestOptionsDto restOptions;

    @PostConstruct
    public void init() {
        Class clazz = dataService.getClassType();
        entityClassName = clazz.getName();
        Entity entity = allEntities.retrieveByClassName(entityClassName);

        RestOptions restOptsFromDb = entity.getRestOptions();

        if (restOptsFromDb == null) {
            throw new IllegalStateException("Rest Facade was created for an entity without any Rest Options."
                + " This should not happen");
        }

        restOptions = restOptsFromDb.toDto();
    }

    @Override
    public List<T> get(QueryParams queryParams) {
        if (!restOptions.isRead()) {
            throw operationNotSupportedEx("READ");
        }
        return dataService.retrieveAll(queryParams);
    }

    @Override
    public void create(T instance) {
        if (!restOptions.isCreate()) {
            throw operationNotSupportedEx("CREATE");
        }
        dataService.create(instance);
    }

    private RestOperationNotSupportedException operationNotSupportedEx(String operation) {
        return new RestOperationNotSupportedException(String.format("%s operation not supported for entity: %s",
                operation, entityClassName));
    }

    public void setDataService(MotechDataService<T> dataService) {
        this.dataService = dataService;
    }

    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }
}
