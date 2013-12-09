package org.motechproject.mds.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.commons.api.CsvConverter;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.web.DraftData;
import org.motechproject.mds.web.SelectData;
import org.motechproject.mds.web.SelectResult;
import org.motechproject.mds.web.comparator.EntityNameComparator;
import org.motechproject.mds.web.comparator.EntityRecordComparator;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.GridSettings;
import org.motechproject.mds.web.domain.Records;
import org.motechproject.mds.web.matcher.EntityMatcher;
import org.motechproject.mds.web.matcher.WIPEntityMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.CharEncoding.UTF_8;

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

    @RequestMapping(value = "/entities/wip", method = RequestMethod.GET)
    @ResponseBody
    public List<EntityDto> getWorkInProgressEntities() {
        List<EntityDto> list = getExampleData().getEntities();

        CollectionUtils.filter(list, new WIPEntityMatcher());

        return list;
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

    @RequestMapping(value = "/entities/{entityId}/draft", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void draft(@PathVariable String entityId, @RequestBody DraftData data) {
        EntityDto entity = getExampleData().getEntity(entityId);

        if (null == entity) {
            throw new EntityNotFoundException();
        } else if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        } else {
            entity.setDraft(true);
        }

        getExampleData().draft(entityId, data);
    }

    @RequestMapping(value = "/entities/{entityId}/abandon", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void abandonChanges(@PathVariable String entityId) {
        if (null == getExampleData().getEntity(entityId)) {
            throw new EntityNotFoundException();
        } else {
            getExampleData().abandonChanges(entityId);
        }
    }

    @RequestMapping(value = "/entities/{entityId}/commit", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void commitChanges(@PathVariable String entityId) {
        if (null == getExampleData().getEntity(entityId)) {
            throw new EntityNotFoundException();
        } else {
            getExampleData().commitChanges(entityId);
        }
    }

    @RequestMapping(value = "/entities/{entityId}/fields", method = RequestMethod.GET)
    @ResponseBody
    public List<FieldDto> getFields(@PathVariable String entityId) {
        if (null == getExampleData().getEntity(entityId)) {
            throw new EntityNotFoundException();
        }

        return getExampleData().getFields(entityId);
    }

    @RequestMapping(value = "entities/{entityId}/fields/{name}", method = RequestMethod.GET)
    @ResponseBody
    public FieldDto getFieldByName(@PathVariable String entityId, @PathVariable String name) {
        if (null == getExampleData().getEntity(entityId)) {
            throw new EntityNotFoundException();
        }

        return getExampleData().findFieldByName(entityId, name);
    }

    @RequestMapping(value = "/entities/{entityId}/instances", method = RequestMethod.GET)
    @ResponseBody
    public Records<EntityRecord> getInstances(@PathVariable String entityId, GridSettings settings) {
        List<EntityRecord> entityList = getExampleData().getEntityRecordsById(entityId);

        boolean sortAscending = settings.getSortDirection() == null || "asc".equals(settings.getSortDirection());

        if (!settings.getSortColumn().isEmpty() && !entityList.isEmpty()) {
            Collections.sort(
                    entityList, new EntityRecordComparator(sortAscending, settings.getSortColumn())
            );
        }

        return new Records<>(settings.getPage(), settings.getRows(), entityList);
    }


    @RequestMapping(value = "/entities/{entityId}/advanced", method = RequestMethod.GET)
    @ResponseBody
    public AdvancedSettingsDto getAdvanced(@PathVariable final String entityId) {
        return getExampleData().getAdvanced(entityId);
    }

    @RequestMapping(value = "/entities/{entityId}/exportInstances", method = RequestMethod.GET)
    public void exportEntityInstances(@PathVariable String entityId, HttpServletResponse response) throws IOException {
        if (null == getExampleData().getEntity(entityId)) {
            throw new EntityNotFoundException();
        }

        String fileName = "Entity_" + entityId + "_instances";
        response.setContentType("text/csv");
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + fileName + ".csv");

        response.getWriter().write(CsvConverter.convertToCSV(prepareForCsvConversion(entityId, getExampleData().getEntityRecordsById(entityId))));
    }

    private List<List<String>> prepareForCsvConversion(String entityId, List<EntityRecord> entityList) {
        List<List<String>> list = new ArrayList<>();

        List<String> fieldNames = new ArrayList<>();
        for (FieldDto field : getExampleData().getFields(entityId)) {
            fieldNames.add(field.getBasic().getDisplayName());
        }
        list.add(fieldNames);

        for (EntityRecord entityRecord : entityList) {
            List<String> fieldValues = new ArrayList<>();
            for (FieldRecord fieldRecord : entityRecord.getFields()) {
                fieldValues.add(fieldRecord.getValue().toString());
            }
            list.add(fieldValues);
        }

        return list;
    }
}
