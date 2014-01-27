package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Entity;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

@Component
class EntityProcessor extends AbstractProcessor {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return Entity.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElements() {
        return getClasses(getAnnotation());
    }

    @Override
    protected void process(AnnotatedElement element) {
        //TODO: process classes
    }
}
