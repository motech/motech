package org.motechproject.mds.web.controller;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.DraftData;
import org.motechproject.mds.dto.DraftResult;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.ex.entity.EntityNotFoundException;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MdsBundleRegenerationService;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    private MdsBundleRegenerationService mdsBundleRegenerationService;
    private EntityService entityService;

    @RequestMapping(value = "/entities/byModule", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<EntityDto>> getEntitiesByModule() {
        Map<String, List<EntityDto>> byModule = new LinkedHashMap<>();
        List<EntityDto> entities = entityService.listEntities(true);

        for (EntityDto entity : entities) {
            if (entity.getModule() == null) {
                if (!byModule.containsKey(NO_MODULE)) {
                    byModule.put(NO_MODULE, new ArrayList<EntityDto>());
                }
                EntityDto entityDto = new EntityDto();
                entityDto.setNonEditable(entity.isNonEditable());
                entityDto.setReadOnlyAccess(entity.isReadOnlyAccess());
                entityDto.setName(entity.getName());

                byModule.get(NO_MODULE).add(entityDto);
            } else {
                if (!byModule.containsKey(entity.getModule())) {
                    byModule.put(entity.getModule(), new ArrayList<EntityDto>());
                }

                if (!entity.isAbstractClass() || entity.isDiscriminated()) {
                    EntityDto entityDto = new EntityDto();
                    entityDto.setNonEditable(entity.isNonEditable());
                    entityDto.setReadOnlyAccess(entity.isReadOnlyAccess());
                    entityDto.setName(entity.getName());

                    byModule.get(entity.getModule()).add(entityDto);
                }
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

    @RequestMapping(value = "/entities/getEntity/{module}/{entityName}", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
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

    @RequestMapping(value = "/entities/getEntityByClassName", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public EntityDto getEntityByClassNameName(@RequestParam(value = "entityClassName", required = true) String entityClassName) {
        EntityDto entity = entityService.getEntityByClassName(entityClassName);

        if (entity == null) {
            throw new EntityNotFoundException(entityClassName);
        }

        return entity;
    }

    @RequestMapping(value = "/entities", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_ANY_MDS_ROLE)
    @ResponseBody
    public List<EntityDto> getAllEntities() {
        return entityService.listEntities();
    }

    @RequestMapping(value = "/entities/getEntityById", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public EntityDto getEntityById(@RequestParam(value = "entityId", required = true) Long entityId) {
        return entityService.getEntity(entityId);
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
        mdsBundleRegenerationService.regenerateMdsDataBundle();
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
        List<String> modulesToRefresh = entityService.commitChanges(entityId);

        // for DDE the parent module must be refreshed
        if (modulesToRefresh.size() > 0) {
            mdsBundleRegenerationService.regenerateMdsDataBundleAfterDdeEnhancement(modulesToRefresh.toArray(new String[modulesToRefresh.size()]));
        } else {
            mdsBundleRegenerationService.regenerateMdsDataBundle();
        }
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
        return entityService.getPossibleFields(entityId);
    }

    @RequestMapping(value = "/entities/{entityId}/entityFields", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public List<FieldDto> getEntityFields(@PathVariable Long entityId) {
        return entityService.getPossibleFields(entityId);
    }

    @RequestMapping(value = "/entities/entityFieldsByClassName", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public List<FieldDto> getEntityFieldsByClassName(@RequestParam(value = "entityClassName", required = true) String entityClassName) {
        return entityService.getEntityFieldsByClassName(entityClassName);
    }

    @RequestMapping(value = "/entities/{entityId}/displayFields", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public List<FieldDto> getDisplayFields(@PathVariable Long entityId) {
        return entityService.getDisplayFields(entityId);
    }

    @RequestMapping(value = "entities/{entityId}/fields/{name}", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_OR_SCHEMA_ACCESS)
    @ResponseBody
    public FieldDto getFieldByName(@PathVariable Long entityId, @PathVariable String name) {
        return entityService.findFieldByName(entityId, name);
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

    @Autowired
    public void setMdsBundleRegenerationService(MdsBundleRegenerationService mdsBundleRegenerationService) {
        this.mdsBundleRegenerationService = mdsBundleRegenerationService;
    }
}
