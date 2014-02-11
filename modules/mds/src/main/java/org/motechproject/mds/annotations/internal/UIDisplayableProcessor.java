package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.util.AnnotationsUtil;
import org.motechproject.mds.util.MemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class UIDisplayableProcessor extends AbstractProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(UIDisplayableProcessor.class);
    private static final Long DEFAULT_VALUE = -1L;

    private Class clazz;

    private Map<String, Long> positions = new LinkedHashMap<>();

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return UIDisplayable.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElements() {
        return AnnotationsUtil.getMembers(
                getAnnotation(), clazz, new MethodPredicate(), new FieldPredicate()
        );
    }

    @Override
    protected void process(AnnotatedElement element) {
        Class<?> classType = MemberUtil.getCorrectType(element);

        if (null != classType) {
            UIDisplayable annotation = AnnotationsUtil.getAnnotation(element, UIDisplayable.class);

            if (null != annotation) {
                String fieldName = MemberUtil.getFieldName(element);
                Long position = annotation.position();

                if (DEFAULT_VALUE.equals(position)) {
                    position = (long) positions.size();
                }

                if (positions.containsValue(position)) {
                    LOGGER.error("The annotation has the position value which is already used");
                } else {
                    positions.put(fieldName, position);
                }
            }
        } else {
            LOGGER.warn("Field type is unknown in: {}", element);
        }
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Map<String, Long> getPositions() {
        return positions;
    }
}
