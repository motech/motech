package org.motechproject.mds.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.web.SelectData;
import org.motechproject.mds.web.SelectResult;
import org.motechproject.mds.web.comparator.EntityNameComparator;
import org.motechproject.mds.web.comparator.EntityRecordComparator;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.comparator.HistoryRecordComparator;
import org.motechproject.mds.web.domain.EntityRecords;
import org.motechproject.mds.web.domain.GridSettings;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.HistoryRecords;
import org.motechproject.mds.web.matcher.EntityMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The <code>EntityController</code> is the Spring Framework Controller used by view layer for
 * executing certain actions on entities.
 *
 * @see SelectData
 * @see SelectResult
 */
@Controller
public class EntityController extends MdsController {

    private static final String NO_MODULE = "(No module)";

    @RequestMapping(value = "/entities/byModule", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<String>> getEntitiesByModule() {
        Map<String, List<String>> byModule = new LinkedHashMap<>();
        List<EntityDto> entities = getAllEntities();

        for (EntityDto entity : entities) {
            if (!byModule.containsKey(entity.getModule()) && entity.getModule() != null) {
                byModule.put(entity.getModule(), new ArrayList<String>());
            }

            if (entity.getModule() != null && !byModule.get(entity.getModule()).contains(entity.getName())) {
                byModule.get(entity.getModule()).add(entity.getName());
            } else if (entity.getModule() == null) {
                if (!byModule.containsKey(NO_MODULE)) {
                    byModule.put(NO_MODULE, new ArrayList<String>());
                }
                byModule.get(NO_MODULE).add(entity.getName());
            }
        }

        return byModule;
    }

    @RequestMapping(value = "/selectEntities", method = RequestMethod.GET)
    @ResponseBody
    public SelectResult<EntityDto> getEntities(SelectData data) {
        List<EntityDto> list = getExampleData().getEntities();

        CollectionUtils.filter(list, new EntityMatcher(data.getTerm()));
        Collections.sort(list, new EntityNameComparator());

        return new SelectResult<>(data, list);
    }

    @RequestMapping(value = "/entities/getEntity/{module}/{entityName}", method = RequestMethod.GET)
    @ResponseBody
    public EntityDto getEntityByModuleAndEntityName(@PathVariable String module, @PathVariable String entityName) {
        List<EntityDto> entities = getAllEntities();
        String moduleName = module.equals(NO_MODULE) ? null : module;

        for (EntityDto entity : entities) {
            if (entity.getModule() == null && moduleName == null && entity.getName().equals(entityName)) {
                return entity;
            } else if (entity.getModule() != null && entity.getModule().equals(moduleName) && entity.getName().equals(entityName)) {
                return entity;
            }
        }

        return null;
    }

    @RequestMapping(value = "/entities", method = RequestMethod.GET)
    @ResponseBody
    public List<EntityDto> getAllEntities() {
        return getExampleData().getEntities();
    }

    @RequestMapping(value = "/entities/{entityId}", method = RequestMethod.GET)
    @ResponseBody
    public EntityDto getEntity(@PathVariable String entityId) {
        EntityDto entity = getExampleData().getEntity(entityId);

        if (null == entity) {
            throw new EntityNotFoundException();
        }

        return entity;
    }

    @RequestMapping(value = "/entities/{entityId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteEntity(@PathVariable final String entityId) {
        EntityDto entity = getExampleData().getEntity(entityId);

        if (null == entity) {
            throw new EntityNotFoundException();
        } else if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        } else {
            getExampleData().removeEntity(entity);
        }
    }

    @RequestMapping(value = "/entities", method = RequestMethod.POST)
    @ResponseBody
    public EntityDto saveEntity(@RequestBody EntityDto entity) {
        if (getExampleData().hasEntityWithName(entity.getName())) {
            throw new EntityAlreadyExistException();
        } else {
            getExampleData().addEntity(entity);
        }

        return entity;
    }

    @RequestMapping(value = "/entities/{entityId}/advanced", method = RequestMethod.GET)
    @ResponseBody
    public AdvancedSettingsDto getAdvanced(@PathVariable final String entityId) {
        AdvancedSettingsDto settings = getExampleData().getAdvanced(entityId);

        if (null == settings) {
            settings = new AdvancedSettingsDto();
            settings.setObjectId(entityId);
        }

        return settings;
    }

    @RequestMapping(value = "/entities/{entityId}/advanced", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveAdvanced(@PathVariable String entityId,
                             @RequestBody AdvancedSettingsDto advanced) {
        EntityDto entity = getExampleData().getEntity(entityId);

        if (null == entity) {
            throw new EntityNotFoundException();
        } else if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }

        getExampleData().saveAdvanced(advanced);
    }

    @RequestMapping(value = "/entities/{entityId}/instances", method = RequestMethod.GET)
    @ResponseBody
    public EntityRecords getInstances(@PathVariable String entityId, GridSettings settings) {
        List<EntityRecord> entityList = getExampleData().getEntityRecordsById(entityId);

        boolean sortAscending = settings.getSortDirection() == null ? true : settings.getSortDirection().equals("asc");

        if (!settings.getSortColumn().isEmpty() && !entityList.isEmpty()) {
            Collections.sort(
                    entityList, new EntityRecordComparator(sortAscending, settings.getSortColumn())
            );
        }

        EntityRecords entityRecords = new EntityRecords(settings.getPage(), settings.getRows(), entityList);

        return entityRecords;
    }

    @RequestMapping(value = "/entities/{instanceId}/history", method = RequestMethod.GET)
    @ResponseBody
    public HistoryRecords getHistory(@PathVariable String instanceId, GridSettings settings) {
        List<HistoryRecord> historyRecordsList = getExampleData().getInstanceHistoryRecordsById(instanceId);

        boolean sortAscending = settings.getSortDirection() == null ? true : settings.getSortDirection().equals("asc");
        if (settings.getSortColumn() != null && !settings.getSortColumn().isEmpty() && !historyRecordsList.isEmpty()) {
            Collections.sort(
                    historyRecordsList, new HistoryRecordComparator(sortAscending, settings.getSortColumn())
            );
        }

        return new HistoryRecords(settings.getPage(), settings.getRows(), historyRecordsList);
    }
}
