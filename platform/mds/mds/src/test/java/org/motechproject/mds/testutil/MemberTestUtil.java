package org.motechproject.mds.testutil;

import org.motechproject.mds.util.MemberUtil;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

import static org.junit.Assert.fail;

public final class MemberTestUtil {

    public static void assertHasField(List<AnnotatedElement> fields, String fieldName) {
        for (AnnotatedElement field : fields) {
            if (fieldName.equals(MemberUtil.getFieldName(field))) {
                return;
            }
        }
        fail(String.format("Field %s not found. Got %s", fieldName, fields.toString()));
    }

    public static void assertHasNoField(List<AnnotatedElement> fields, String fieldName) {
        for (AnnotatedElement field : fields) {
            if (fieldName.equals(MemberUtil.getFieldName(field))) {
                fail(String.format("Field %s found. Got %s", fieldName, fields.toString()));
            }
        }
    }

    private MemberTestUtil() {

    }
}
