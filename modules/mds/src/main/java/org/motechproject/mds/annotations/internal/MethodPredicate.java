package org.motechproject.mds.annotations.internal;

import org.apache.commons.collections.Predicate;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.util.AnnotationsUtil;
import org.motechproject.mds.util.MemberUtil;

import java.lang.reflect.Method;

import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

class MethodPredicate implements Predicate {

    @Override
    public boolean evaluate(Object object) {
        boolean match = object instanceof Method;

        if (match) {
            Method method = (Method) object;
            boolean isNotFromObject = method.getDeclaringClass() != Object.class;
            boolean isGetter = startsWithIgnoreCase(method.getName(), MemberUtil.GETTER_PREFIX);
            boolean isSetter = startsWithIgnoreCase(method.getName(), MemberUtil.SETTER_PREFIX);
            boolean hasIgnoreAnnotation = AnnotationsUtil.hasAnnotation(method, Ignore.class);

            match = (isNotFromObject && (isGetter || isSetter)) && !hasIgnoreAnnotation;
        }

        return match;
    }
}
