package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.RestIgnore;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.MemberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.motechproject.mds.annotations.internal.PredicateUtil.restIgnore;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAME;

/**
 * The <code>RestIgnoreProcessor</code> provides a mechanism for finding fields
 * annotated with {@link org.motechproject.mds.annotations.RestIgnore} inside the
 * class with {@link org.motechproject.mds.annotations.Entity} annotation.
 *
 * @see org.motechproject.mds.annotations.RestIgnore
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
public class RestIgnoreProcessor extends AbstractListProcessor<RestIgnore, String> {

    private EntityService entityService;

    private EntityDto entity;
    private Class clazz;

    @Override
    protected List<? extends AnnotatedElement> getElementsToProcess() {
        List<Member> members = ReflectionsUtil.getFilteredMembers(clazz, restIgnore());
        List<AnnotatedElement> elements = new ArrayList<>(members.size());
        for (Member member : members) {
            if (member instanceof AnnotatedElement) {
                elements.add((AnnotatedElement) member);
            }
        }
        return elements;
    }

    @Override
    protected void process(AnnotatedElement element) {
        Field fieldAnnotation = ReflectionsUtil.getAnnotationClassLoaderSafe(element, clazz, Field.class);
        String fieldName = MemberUtil.getFieldName(element);
        String field = ReflectionsUtil.getAnnotationValue(
                fieldAnnotation, NAME, fieldName
        );

        add(field);
    }

    @Override
    protected void afterExecution() {
        Collection<FieldDto> fields = entityService.getEntityFields(entity.getId());
        Collection<String> ignoredFieldNames = getElements();
        List<Number> fieldIds = new ArrayList<>();

        for (FieldDto field : fields) {
            if (!ignoredFieldNames.contains(field.getBasic().getName())) {
                fieldIds.add(field.getId());
            }
        }

        RestOptionsDto restOptions = entityService.getAdvancedSettings(entity.getId(), true).getRestOptions();
        restOptions.setFieldIds(fieldIds);
        entityService.updateRestOptions(entity.getId(), restOptions);
    }

    @Override
    public Class<RestIgnore> getAnnotationType() {
        return RestIgnore.class;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public void setEntity(EntityDto entity) {
        this.entity = entity;
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }
}
