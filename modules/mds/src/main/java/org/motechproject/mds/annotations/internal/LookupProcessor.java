package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Lookup;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

@Component
class LookupProcessor extends AbstractProcessor {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return Lookup.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElements() {
        return getMethods(getAnnotation());
    }

    @Override
    protected void process(AnnotatedElement element) {
        //TODO: process classes
    }
}
