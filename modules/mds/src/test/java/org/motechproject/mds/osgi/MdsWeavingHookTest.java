package org.motechproject.mds.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.javassist.MotechClassPool;
import org.osgi.framework.hooks.weaving.WovenClass;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsWeavingHookTest {

    private static final String TEST_CLASS = "org.motechproject.test.MdsTest";

    private MdsWeavingHook mdsWeavingHook = new MdsWeavingHook();

    @Mock
    private WovenClass wovenClass;

    @Test
    public void shouldWeaveClassesForDdeAndAddImports() {
        MotechClassPool.registerEnhancedClassData(new ClassData(TEST_CLASS, "testClassContent".getBytes()));
        List<String> dynamicImports = new ArrayList<>(asList("one.two.three"));

        when(wovenClass.getClassName()).thenReturn(TEST_CLASS);
        when(wovenClass.getDynamicImports()).thenReturn(dynamicImports);

        mdsWeavingHook.weave(wovenClass);

        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        verify(wovenClass).setBytes(captor.capture());
        assertArrayEquals("testClassContent".getBytes(), captor.getValue());

        assertEquals(asList("one.two.three", "javax.jdo", "javax.jdo.spi"), dynamicImports);
    }
}
