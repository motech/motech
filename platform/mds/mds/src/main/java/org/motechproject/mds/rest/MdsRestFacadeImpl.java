package org.motechproject.mds.rest;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.ex.rest.RestBadBodyFormatException;
import org.motechproject.mds.ex.rest.RestOperationNotSupportedException;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.MotechDataService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This {@link org.motechproject.mds.rest.MdsRestFacade} implementation
 * retrieves REST related metadata on initialization. It uses an instance of
 * {@link org.motechproject.mds.service.MotechDataService} for operations and
 * the jackson JSON library for parsing InputStreams.
 * @param <T>
 */
public class MdsRestFacadeImpl<T> implements MdsRestFacade<T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MotechDataService<T> dataService;
    private AllEntities allEntities;
    private Class<T> entityClass;

    private RestOptionsDto restOptions;

    @PostConstruct
    public void init() {
        entityClass = dataService.getClassType();
        Entity entity = allEntities.retrieveByClassName(entityClass.getName());

        RestOptions restOptsFromDb = entity.getRestOptions();

        if (restOptsFromDb == null) {
            restOptions = new RestOptionsDto();
        } else {
            restOptions = restOptsFromDb.toDto();
        }
    }

    @Override
    public List<T> get(QueryParams queryParams) {
        if (!restOptions.isRead()) {
            throw operationNotSupportedEx("READ");
        }
        return dataService.retrieveAll(queryParams);
    }

    @Override
    public T get(Long id) {
        if(!restOptions.isRead()) {
            throw operationNotSupportedEx("READ");
        }
        return dataService.findById(id);
    }

    @Override
    public void create(InputStream instanceBody) {
        if (!restOptions.isCreate()) {
            throw operationNotSupportedEx("CREATE");
        }

        try {
            T instance = OBJECT_MAPPER.readValue(instanceBody, entityClass);
            dataService.create(instance);
        } catch (IOException e) {
            throw badBodyFormatException(e);
        }
    }

    @Override
    public void update(InputStream instanceBody) {
        if (!restOptions.isUpdate()) {
            throw operationNotSupportedEx("UPDATE");
        }

        try {
            T instance = OBJECT_MAPPER.readValue(instanceBody,entityClass);
            dataService.updateFromTransient(instance);
        } catch (IOException e) {
            throw badBodyFormatException(e);
        }
    }

    @Override
    public void delete(Long id) {
        if (!restOptions.isDelete()) {
            throw operationNotSupportedEx("DELETE");
        }

        dataService.delete("id", id);
    }

    private RestOperationNotSupportedException operationNotSupportedEx(String operation) {
        return new RestOperationNotSupportedException(String.format("%s operation not supported for entity: %s",
                operation, entityClass));
    }

    private RestBadBodyFormatException badBodyFormatException(IOException ioException) {
        return new RestBadBodyFormatException("Unable to parse provided body to "
                + dataService.getClassType().getName(), ioException);
    }

    public void setDataService(MotechDataService<T> dataService) {
        this.dataService = dataService;
    }

    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }
}
