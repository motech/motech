package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.MemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.motechproject.mds.annotations.internal.PredicateUtil.uiDisplayable;

/**
 * The <code>UIDisplayableProcessor</code> provides a mechanism to finding fields or methods with
 * the {@link org.motechproject.mds.annotations.UIDisplayable} annotation inside the class with the
 * {@link org.motechproject.mds.annotations.Entity} annotation.
 *
 * @see org.motechproject.mds.annotations.UIDisplayable
 * @see org.motechproject.mds.annotations.Entity
 */
@Component
class UIDisplayableProcessor extends AbstractMapProcessor<UIDisplayable, String, Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIDisplayableProcessor.class);
    private static final Long DEFAULT_VALUE = -1L;

    private EntityService entityService;

    private EntityDto entity;
    private Class clazz;

    @Override
    public Class<UIDisplayable> getAnnotationType() {
        return UIDisplayable.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElementsToProcess() {
        List<Member> members = ReflectionsUtil.getFilteredMembers(clazz, uiDisplayable());
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
        Class<?> classType = MemberUtil.getCorrectType(element);

        if (null != classType) {
            UIDisplayable annotation = ReflectionsUtil.getAnnotationClassLoaderSafe(element, classType, UIDisplayable.class);

            if (null != annotation) {
                String fieldName = MemberUtil.getFieldName(element);
                Long position = annotation.position();

                if (DEFAULT_VALUE.equals(position)) {
                    // assign negative value, but respect order in which field appeared in processor
                    put(fieldName, Long.MIN_VALUE + getElements().size());
                } else if (getElements().containsValue(position)) {
                    LOGGER.error("The annotation has the position value which is already used. Assigning default value");
                    // assign negative value, but respect order in which field appeared in processor
                    put(fieldName, Long.MIN_VALUE + getElements().size());
                } else {
                    put(fieldName, position);
                }
            }
        } else {
            LOGGER.warn("Field type is unknown in: {}", element);
        }
    }

    @Override
    protected void afterExecution() {
        SortedMap<Long, String> positions = new TreeMap<>();
        Map<String, Long> uiDisplayable = new HashMap<>();
        // invert key/value; we have one to one relation, so we can do it
        for (Map.Entry<String, Long> element : getElements().entrySet()) {
            positions.put(element.getValue(), element.getKey());
        }
        for (long i = 0; i < getElements().size(); i++) {
            if (positions.containsKey(i)) {
                uiDisplayable.put(positions.get(i), i);
                positions.remove(i);
            } else {
                Long smallestKey = positions.firstKey();
                if (smallestKey >= getElements().size()) {
                    LOGGER.warn("The annotation has the position value which is greater than total number of fields.");
                }
                uiDisplayable.put(positions.get(smallestKey), i);
                positions.remove(smallestKey);
            }
        }

        entityService.addDisplayedFields(entity, uiDisplayable);
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
