package org.motechproject.mds.annotations.internal;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.ReadAccess;
import org.motechproject.mds.domain.MdsEntity;
import org.motechproject.mds.domain.MdsVersionedEntity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.TrackingDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.helper.EntityDefaultFieldsHelper;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.SecurityMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import javax.jdo.annotations.Version;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;

import static org.motechproject.mds.util.Constants.AnnotationFields.MAX_FETCH_DEPTH;

/**
 * The <code>EntityAnnotationProcessor</code> provides a mechanism, allowing adding public classes from other
 * modules as entities in the MDS module and extending them.
 *
 * @see org.motechproject.mds.annotations.EntityExtension
 * @see org.motechproject.mds.annotations.Entity
 */
public abstract class EntityAnnotationProcessor<T extends Annotation> extends AbstractListProcessor<T, EntityDto>{


    private UIFilterableProcessor uiFilterableProcessor;
    private UIDisplayableProcessor uiDisplayableProcessor;
    private RestIgnoreProcessor restIgnoreProcessor;
    private RestOperationsProcessor restOperationsProcessor;
    private CrudEventsProcessor crudEventsProcessor;
    private NonEditableProcessor nonEditableProcessor;

    @Override
    protected Set<? extends AnnotatedElement> getElementsToProcess() {
        return ReflectionsUtil.getClasses(getAnnotationType(), getBundle());
    }

    protected void addVersionMetadata(Collection<FieldDto> fields, String versionField) {
        if (StringUtils.isNotBlank(versionField)) {
            for (FieldDto fieldDto : fields) {
                if (fieldDto.getBasic().getName().equals(versionField)) {
                    fieldDto.addMetadata(new MetadataDto(Constants.MetadataKeys.VERSION_FIELD, Constants.Util.TRUE));
                }
            }
        }
    }

    protected String getVersionFieldName(Class clazz) {
        if (MdsVersionedEntity.class.getName().equalsIgnoreCase(clazz.getSuperclass().getName())) {
            return "instanceVersion";
        }

        Class<Version> verAnn = ReflectionsUtil.getAnnotationClass(clazz, Version.class);
        Version versionAnnotation = AnnotationUtils.findAnnotation(clazz, verAnn);
        if (versionAnnotation != null && versionAnnotation.extensions().length !=0 && versionAnnotation.extensions()[0].key().equals("field-name")) {
            return versionAnnotation.extensions()[0].value();
        }

        return null;
    }

    protected void setMaxFetchDepth(EntityDto entity, Annotation annotation) {
        int maxFetchDepth = Integer.parseInt(ReflectionsUtil.getAnnotationValue(annotation, MAX_FETCH_DEPTH));
        if (maxFetchDepth != Constants.FetchDepth.MDS_DEFAULT) {
            entity.setMaxFetchDepth(maxFetchDepth);
        }
    }

    protected void addDefaultFields(EntityDto entity, Collection<FieldDto> fields) {
        if (!MdsEntity.class.getName().equalsIgnoreCase(entity.getSuperClass()) &&
                !MdsVersionedEntity.class.getName().equalsIgnoreCase(entity.getSuperClass())) {
            fields.addAll(EntityDefaultFieldsHelper.defaultFields(getSchemaHolder()));
        }
    }

    protected void updateResults(EntityProcessorOutput entityProcessorOutput, Class<?> clazz, Collection<FieldDto> fields,
                                 RestOptionsDto restOptions, TrackingDto tracking, String versionField) {
        entityProcessorOutput.setFieldProcessingResult(fields);

        entityProcessorOutput.setUiFilterableProcessingResult(findFilterableFields(clazz));
        entityProcessorOutput.setUiDisplayableProcessingResult(findDisplayedFields(clazz));
        entityProcessorOutput.setTrackingProcessingResult(processCrudEvents(clazz, tracking));

        entityProcessorOutput.setRestProcessingResult(restOptions);

        Map<String, Boolean> nonEditableFields = findNonEditableFields(clazz);
        //we must set non editable for version field
        if (StringUtils.isNotBlank(versionField)) {
            nonEditableFields.put(versionField, true);
        }
        entityProcessorOutput.setNonEditableProcessingResult(nonEditableFields);
    }

    protected void updateUiChangedFields(Collection<FieldDto> fieldsToUpdate, String entityClassName) {
        if (getSchemaHolder().getEntityByClassName(entityClassName) != null) {
            List<FieldDto> currentFields = getSchemaHolder().getFields(entityClassName);
            for (FieldDto field : fieldsToUpdate) {
                FieldDto currentField = getCurrentField(currentFields, field.getBasic().getName());
                if (currentField != null && currentField.isUiChanged()) {
                    field.setUiFilterable(currentField.isUiFilterable());
                    field.setUiChanged(currentField.isUiChanged());
                }
            }
        }
    }

    private FieldDto getCurrentField(List<FieldDto> currentFields, String fieldName) {
        for (FieldDto field : currentFields) {
            if (field.getBasic().getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    protected RestOptionsDto processRestOperations(Class clazz, RestOptionsDto restOptions) {
        restOperationsProcessor.setClazz(clazz);
        restOperationsProcessor.setRestOptions(restOptions);
        restOperationsProcessor.execute(getBundle(), getSchemaHolder());
        return restOperationsProcessor.getProcessingResult();
    }

    private TrackingDto processCrudEvents(Class clazz, TrackingDto tracking) {
        crudEventsProcessor.setClazz(clazz);
        crudEventsProcessor.setTrackingDto(tracking);
        crudEventsProcessor.execute(getBundle(), getSchemaHolder());
        return crudEventsProcessor.getProcessingResult();
    }

    private Collection<String> findFilterableFields(Class clazz) {
        uiFilterableProcessor.setClazz(clazz);
        uiFilterableProcessor.execute(getBundle(), getSchemaHolder());
        return uiFilterableProcessor.getProcessingResult();
    }

    private Map<String, Long> findDisplayedFields(Class clazz) {
        uiDisplayableProcessor.setClazz(clazz);
        uiDisplayableProcessor.execute(getBundle(), getSchemaHolder());
        return uiDisplayableProcessor.getProcessingResult();
    }

    private Map<String, Boolean> findNonEditableFields(Class clazz) {
        nonEditableProcessor.setClazz(clazz);
        nonEditableProcessor.execute(getBundle(), getSchemaHolder());
        return nonEditableProcessor.getProcessingResult();
    }

    protected RestOptionsDto findRestFields(Class clazz, RestOptionsDto restOptions, Collection<FieldDto> fields) {
        restIgnoreProcessor.setClazz(clazz);
        restIgnoreProcessor.setRestOptions(restOptions);
        restIgnoreProcessor.setFields(new ArrayList<>(fields));
        restIgnoreProcessor.execute(getBundle(), getSchemaHolder());
        return restIgnoreProcessor.getProcessingResult();
    }

    protected void setSecurityOptions(AnnotatedElement element, EntityDto entity) {
        Access access = element.getAnnotation(Access.class);
        ReadAccess readAccess = element.getAnnotation(ReadAccess.class);
        if (null != access && !entity.isSecurityOptionsModified()) {
            Set<String> securityMembers = returnSecurityMembersForSecurityMode(access.value(), access.members(), "Access");
            entity.setSecurityMode(access.value());
            entity.setSecurityMembers(securityMembers);
            if(null == readAccess) {
                entity.setReadOnlySecurityMode(SecurityMode.NO_ACCESS);
            }
        }

        if(null != readAccess && !entity.isSecurityOptionsModified()) {
            Set<String> readOnlySecurityMembers = returnSecurityMembersForSecurityMode(readAccess.value(), readAccess.members(), "ReadAccess");
            entity.setReadOnlySecurityMode(readAccess.value());
            if(entity.getSecurityMode() == SecurityMode.EVERYONE) {
                entity.setSecurityMode(SecurityMode.NO_ACCESS);
            }
            entity.setReadOnlySecurityMembers(readOnlySecurityMembers);
        }
    }

    private Set<String> returnSecurityMembersForSecurityMode(SecurityMode securityMode, String[] securityMembersArray, String annotationName) {
        Boolean hasMembers = securityMembersArray != null && securityMembersArray.length > 0;
        Set<String> securityMembers;
        if (securityMode == SecurityMode.USERS || securityMode == SecurityMode.PERMISSIONS) {
            if (hasMembers) {
                securityMembers = new HashSet<>(Arrays.asList(securityMembersArray));
            } else {
                throw new IllegalArgumentException(
                        "Failed to process " + annotationName+ " annotation: the security mode is set to "
                                + securityMode + " but there are no members specified."
                );
            }
        } else {
            securityMembers = new HashSet<>();
            if (hasMembers) {
                throw new IllegalArgumentException(
                        "Failed to process " + annotationName + " annotation: the members attribute can be only used with USERS or PERMISSIONS security mode."
                );
            }
        }
        return securityMembers;
    }




    @Autowired
    public void setUIFilterableProcessor(UIFilterableProcessor uiFilterableProcessor) {
        this.uiFilterableProcessor = uiFilterableProcessor;
    }

    @Autowired
    public void setUIDisplayableProcessor(UIDisplayableProcessor uiDisplayableProcessor) {
        this.uiDisplayableProcessor = uiDisplayableProcessor;
    }

    @Autowired
    public void setRestIgnoreProcessor(RestIgnoreProcessor restIgnoreProcessor) {
        this.restIgnoreProcessor = restIgnoreProcessor;
    }

    @Autowired
    public void setRestOperationsProcessor(RestOperationsProcessor restOperationsProcessor) {
        this.restOperationsProcessor = restOperationsProcessor;
    }

    @Autowired
    public void setCrudEventsProcessor(CrudEventsProcessor crudEventsProcessor) {
        this.crudEventsProcessor = crudEventsProcessor;
    }

    @Autowired
    public void setNonEditableProcessor(NonEditableProcessor nonEditableProcessor) {
        this.nonEditableProcessor = nonEditableProcessor;
    }
}
