package org.motechproject.commons.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.model.ExtensibleDataObject;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AbstractDataProviderLookupTest {
    private class TestDataProvider extends AbstractDataProvider {

        @Override
        public List<Class<?>> getSupportClasses() {
            return Arrays.asList(MotechObject.class, MotechException.class);
        }

        @Override
        public String getPackageRoot() {
            return "org.motechproject.commons.api";
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Object lookup(String clazz, Map<String, String> lookupFields) {
            return null;
        }
    }

    @Mock
    private Resource resource;

    private TestDataProvider testDataProvider = new TestDataProvider();

    @Before
    public void setup() throws Exception {
        initMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSetBodyWhenResourceIsNull() {
        testDataProvider.setBody(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSetBodyWhenResourceNotExists() {
        when(resource.exists()).thenReturn(false);

        testDataProvider.setBody(resource);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSetBodyWhenResourceCantBeRead() {
        when(resource.exists()).thenReturn(true);
        when(resource.isReadable()).thenReturn(false);

        testDataProvider.setBody(resource);
    }

    @Test
    public void shouldSetBody() throws IOException {
        String body = "{name='test', objects=[]}";
        InputStream inputStream = new ByteArrayInputStream(body.getBytes());

        when(resource.exists()).thenReturn(true);
        when(resource.isReadable()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(inputStream);

        testDataProvider.setBody(resource);

        String actual = testDataProvider.getBody();
        String expected = body.replaceAll("(^\\s+|\\s+$)", "");

        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnFalseWhenClassIsNotAssignable() {
        Class<?> check = ExtensibleDataObject.class;
        List<Class<?>> supportClasses = testDataProvider.getSupportClasses();

        assertFalse(testDataProvider.isAssignable(check, supportClasses));
    }

    @Test
    public void shouldReturnTrueWhenClassIsAssignable() {
        Class<?> check = MotechException.class;
        List<Class<?>> supportClasses = testDataProvider.getSupportClasses();

        assertTrue(testDataProvider.isAssignable(check, supportClasses));
    }

    @Test
    public void shouldReturnFalseWhenClassIsNotSupported() {
        String clazz = ExtensibleDataObject.class.getName();

        assertFalse(testDataProvider.supports(clazz));
    }

    @Test
    public void shouldReturnFalseWhenClassNotFoundExceptionAppeared() {
        String clazz = "org.motechproject.tasks.domain.DataProvider";

        assertFalse(testDataProvider.supports(clazz));
    }

    @Test
    public void shouldReturnTrueWhenClassIsSupported() {
        String clazz = MotechObject.class.getSimpleName();

        assertTrue(testDataProvider.supports(clazz));
    }

}
