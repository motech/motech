package org.motechproject.mds.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.DraftResult;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.web.DraftData;
import org.motechproject.mds.web.SelectData;
import org.motechproject.mds.web.SelectResult;
import org.motechproject.mds.web.comparator.EntityNameComparator;
import org.motechproject.mds.web.matcher.EntityMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.mds.util.Constants.Roles;

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

    private EntityService entityService;

    @RequestMapping(value = "/entities/byModule", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
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
    @PreAuthorize(Roles.HAS_ANY_MDS_ROLE)
    @ResponseBody
    public List<EntityDto> getWorkInProgressEntities() {
        return entityService.listWorkInProgress();
    }

    @RequestMapping(value = "/selectEntities", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public SelectResult<EntityDto> getEntities(SelectData data) {
        List<EntityDto> list = entityService.listEntities();

        CollectionUtils.filter(list, new EntityMatcher(data.getTerm()));
        Collections.sort(list, new EntityNameComparator());

        return new SelectResult<>(data, list);
    }

    @RequestMapping(value = "/entities/getEntity", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public EntityDto getEntityByModuleAndEntityName(@RequestBody Map<String, String> params) {
        List<EntityDto> entities = getAllEntities();
        String moduleName = NO_MODULE.equals(params.get("module")) ? null : params.get("module");

        for (EntityDto entity : entities) {
            if (entity.getModule() == null && moduleName == null && entity.getName().equals(params.get("entityName"))) {
                return entity;
            } else if (entity.getModule() != null && entity.getModule().equals(moduleName) &&
                    entity.getName().equals(params.get("entityName"))) {
                return entity;
            }
        }

        return null;
    }

    @RequestMapping(value = "/entities", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_ANY_MDS_ROLE)
    @ResponseBody
    public List<EntityDto> getAllEntities() {
        return entityService.listEntities();
    }

    @RequestMapping(value = "/entities/{entityId}", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public EntityDto getEntity(@PathVariable Long entityId) {
        return entityService.getEntityForEdit(entityId);
    }

    @RequestMapping(value = "/entities/{entityId}", method = RequestMethod.DELETE)
    @PreAuthorize(Roles.HAS_SCHEMA_ACCESS)
    @ResponseBody
    public void deleteEntity(@PathVariable final Long entityId) {
        entityService.deleteEntity(entityId);
    }

    @RequestMapping(value = "/entities", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_SCHEMA_ACCESS)
    @ResponseBody
    public EntityDto saveEntity(@RequestBody EntityDto entity) throws IOException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        EntityDto created = entityService.createEntity(entity);
        return entityService.getEntityForEdit(created.getId());
    }

    @RequestMapping(value = "/entities/{entityId}/draft", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_SCHEMA_ACCESS)
    @ResponseBody
    public DraftResult draft(@PathVariable Long entityId, @RequestBody DraftData data) {
        return entityService.saveDraftEntityChanges(entityId, data);
    }

    @RequestMapping(value = "/entities/{entityId}/abandon", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_SCHEMA_ACCESS)
    @ResponseStatus(HttpStatus.OK)
    public void abandonChanges(@PathVariable Long entityId) {
        entityService.abandonChanges(entityId);
    }

    @RequestMapping(value = "/entities/{entityId}/commit", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_SCHEMA_ACCESS)
    @ResponseStatus(HttpStatus.OK)
    public void commitChanges(@PathVariable Long entityId) {
        entityService.commitChanges(entityId);
    }

    @RequestMapping(value = "/entities/{entityId}/update", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_SCHEMA_ACCESS)
    @ResponseBody
    public EntityDto updateDraft(@PathVariable Long entityId) {
        return entityService.updateDraft(entityId);
    }

    @RequestMapping(value = "/entities/{entityId}/fields", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public List<FieldDto> getFields(@PathVariable Long entityId) {
        return entityService.getFields(entityId);
    }

    @RequestMapping(value = "/entities/{entityId}/entityFields", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public List<FieldDto> getEntityFields(@PathVariable Long entityId) {
        return entityService.getEntityFields(entityId);
    }

    @RequestMapping(value = "/entities/{entityId}/displayFields", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public List<FieldDto> getDisplayFields(@PathVariable Long entityId) {
        return entityService.getDisplayFields(entityId);
    }

    @RequestMapping(value = "entities/{entityId}/fields", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public FieldDto getFieldByName(@PathVariable Long entityId, @RequestBody Map<String, String> params) {
        return entityService.findFieldByName(entityId, params.get("fieldName"));
    }

    @RequestMapping(value = "/entities/{entityId}/advanced", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public AdvancedSettingsDto getAdvanced(@PathVariable final Long entityId) {
        return entityService.getAdvancedSettings(entityId);
    }

    @RequestMapping(value = "/entities/{entityId}/advancedCommited", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public AdvancedSettingsDto getCommitedAdvanced(@PathVariable final Long entityId) {
        return entityService.getAdvancedSettings(entityId, true);
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }
}
