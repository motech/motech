package org.motechproject.cmslite.api;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.commons.api.MotechObject;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CMSDataProviderTest {
    private static final String FIELD_KEY = "id";
    private static final String FIELD_VALUE = "12345";

    private static Map<String, String> lookupFields;

    @Mock
    private StringContent stringContent;

    @Mock
    private StreamContent streamContent;

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private CMSLiteService cmsLiteService;

    private CMSDataProvider provider;

    @BeforeClass
    public static void setLookupFields(){
        lookupFields = new HashMap<>();
        lookupFields.put(FIELD_KEY, FIELD_VALUE);
    }

    @Before
    public void setUp() throws Exception{
        initMocks(this);

        provider = new CMSDataProvider(resourceLoader);
        provider.setCmsLiteService(cmsLiteService);
    }

    @Test
    public void shouldReturnNullWhenClassIsNotSupported() {
        // given
        String clazz = MotechObject.class.getSimpleName();

        // when
        Object object = provider.lookup(clazz, lookupFields);

        // then
        assertNull(object);
    }

    @Test
    public void shouldReturnNullWhenMapNotContainsSupportedField() {
        // given
        String clazz = StringContent.class.getSimpleName();
        HashMap<String, String> fields = new HashMap<>();

        // when
        Object object = provider.lookup(clazz, fields);

        // then
        assertNull(object);
    }

    @Test
    public void shouldReturnNullWhenListIsNull() {
        // given
        String stringContentClass = StringContent.class.getSimpleName();
        String streamContentClass = StreamContent.class.getSimpleName();

        // when
        Object stringContent = provider.lookup(stringContentClass, lookupFields);
        Object streamContent = provider.lookup(streamContentClass, lookupFields);

        // then
        assertNull(stringContent);
        assertNull(streamContent);
    }

    @Test
    public void shouldReturnNullWhenListIsEmpty() {
        // given
        String stringContentClass = StringContent.class.getSimpleName();
        String streamContentClass = StreamContent.class.getSimpleName();

        // when
        Object stringContent = provider.lookup(stringContentClass, lookupFields);
        Object streamContent = provider.lookup(streamContentClass, lookupFields);

        // then
        assertNull(stringContent);
        assertNull(streamContent);
    }

    @Test
    public void shouldReturnObject() {
        // given
        String stringContentClass = StringContent.class.getSimpleName();
        String streamContentClass = StreamContent.class.getSimpleName();

        when(cmsLiteService.getStringContent(FIELD_VALUE)).thenReturn(stringContent);
        when(cmsLiteService.getStreamContent(FIELD_VALUE)).thenReturn(streamContent);

        // when
        StringContent stringContent = (StringContent) provider.lookup(stringContentClass, lookupFields);
        StreamContent streamContent = (StreamContent) provider.lookup(streamContentClass, lookupFields);

        // then
        assertEquals(this.stringContent, stringContent);
        assertEquals(this.streamContent, streamContent);
    }
}
