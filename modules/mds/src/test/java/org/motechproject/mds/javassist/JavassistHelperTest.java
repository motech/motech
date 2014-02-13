package org.motechproject.mds.javassist;

import javassist.CtClass;
import javassist.CtField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Field;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JavassistHelperTest {

    @Mock
    private CtClass ctClass;

    @Mock
    private CtField ctField;

    @Test
    public void shouldCreateClassPathEntries() {
        assertEquals("this/is/a/test/package.class", JavassistHelper.toClassPath("this.is.a.test.package"));
        assertEquals("java/lang/String.class", JavassistHelper.toClassPath(String.class));
        assertEquals("java/lang/String.class", JavassistHelper.toClassPath(String.class.getName()));
    }

    @Test
    public void shouldCreateProperGenericSignatures() {
        assertEquals("Ljava/util/List<Lorg/motechproject/mds/domain/Field;>;",
                JavassistHelper.genericFieldSignature(List.class, Field.class));
        assertEquals("Ljava/util/List<Lorg/motechproject/mds/domain/Field;>;",
                JavassistHelper.genericFieldSignature(List.class.getName(), Field.class.getName()));
    }

    @Test
    public void shouldFindFieldByName() {
        assertFalse(JavassistHelper.containsDelcaredField(ctClass, "name"));

        when(ctClass.getDeclaredFields()).thenReturn(new CtField[0]);
        assertFalse(JavassistHelper.containsDelcaredField(ctClass, "name"));

        when(ctField.getName()).thenReturn("name");
        when(ctClass.getDeclaredFields()).thenReturn(new CtField[]{ctField});
        assertTrue(JavassistHelper.containsDelcaredField(ctClass, "name"));
    }
}
