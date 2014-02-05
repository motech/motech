package org.motechproject.mds.javassist;

import org.junit.Test;
import org.motechproject.mds.domain.FieldMapping;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JavassistHelperTest {

    @Test
    public void shouldCreateClassPathEntries() {
        assertEquals("this/is/a/test/package", JavassistHelper.toClassPath("this.is.a.test.package"));
        assertEquals("java/lang/String", JavassistHelper.toClassPath(String.class));
        assertEquals("java/lang/String", JavassistHelper.toClassPath(String.class.getName()));
    }

    @Test
    public void shouldCreateProperGenericSignatures() {
        assertEquals("Ljava/util/List<Lorg/motechproject/mds/domain/FieldMapping;>;",
                JavassistHelper.genericFieldSignature(List.class, FieldMapping.class));
        assertEquals("Ljava/util/List<Lorg/motechproject/mds/domain/FieldMapping;>;",
                JavassistHelper.genericFieldSignature(List.class.getName(), FieldMapping.class.getName()));
    }
}
