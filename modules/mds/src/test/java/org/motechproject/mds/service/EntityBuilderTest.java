package org.motechproject.mds.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.motechproject.mds.service.EntityBuilder.PACKAGE;

@RunWith(MockitoJUnitRunner.class)
public class EntityBuilderTest {
    private static final String SIMPLE_NAME = "Sample";
    private static final String CLASS_NAME = String.format("%s.%s", PACKAGE, SIMPLE_NAME);

    @Mock
    private JDOClassLoader jdoClassLoader;

    @Test
    public void shouldCreateEmptyClassAndAndToClassLoader() throws Exception {
        EntityBuilder builder = new EntityBuilder()
                .withSingleName(SIMPLE_NAME)
                .withClassLoader(jdoClassLoader);

        assertEquals(CLASS_NAME, builder.getClassName());
        assertArrayEquals(new byte[0], builder.getClassBytes());

        builder.build();

        assertTrue(builder.getClassBytes().length > 0);

        verify(jdoClassLoader).defineClass(CLASS_NAME, builder.getClassBytes());
    }
}
