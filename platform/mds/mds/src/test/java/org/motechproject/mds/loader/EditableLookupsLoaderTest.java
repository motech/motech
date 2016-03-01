package org.motechproject.mds.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.mds.annotations.internal.EntityProcessorOutput;
import org.motechproject.mds.annotations.internal.MDSProcessorOutput;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.JsonLookupDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.exception.loader.MalformedLookupsJsonException;
import org.motechproject.mds.lookup.EntityLookups;
import org.motechproject.mds.service.JsonLookupService;
import org.osgi.framework.Bundle;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EditableLookupsLoaderTest {

    private static final Gson GSON = new GsonBuilder().create();

    private static final URL VALID_JSON = EditableLookupsLoaderTest.class.getResource("/json/valid-lookups.json");
    private static final URL INVALID_JSON = EditableLookupsLoaderTest.class.getResource("/json/invalid-lookups.json");
    private static final URL EMPTY_JSON = EditableLookupsLoaderTest.class.getResource("/json/empty-lookups.json");

    private static final String CLASS_NAME = "org.motechproject.testbundle.TestClass";
    private static final String ORIGIN_LOOKUP_NAME = "Find by Value";
    private static final String MDS_LOOKUPS_JSON = "mds-lookups.json";

    @Mock
    private JsonLookupService jsonLookupService;

    @Mock
    private Bundle bundle;

    @Captor
    private ArgumentCaptor<JsonLookupDto> jsonLookupCaptor;

    private EditableLookupsLoader editableLookupsLoader;

    private MDSProcessorOutput output;

    @Before
    public void setUp() {
        initMocks(this);
        prepareOutput();
        editableLookupsLoader = new EditableLookupsLoader(jsonLookupService);
    }

    @Test
    public void shouldAddLookupIfJsonIsValid() throws Exception {

        JsonLookupDto expectedJsonLookup = new JsonLookupDto(CLASS_NAME, ORIGIN_LOOKUP_NAME);
        LookupDto expectedLookup = loadLookup();

        when(bundle.getResource(MDS_LOOKUPS_JSON)).thenReturn(VALID_JSON);
        when(jsonLookupService.exists(CLASS_NAME, ORIGIN_LOOKUP_NAME)).thenReturn(false);

        editableLookupsLoader.addEditableLookups(output, bundle);

        verify(jsonLookupService, times(1)).exists(matches(CLASS_NAME), matches(ORIGIN_LOOKUP_NAME));
        verify(jsonLookupService, times(1)).createJsonLookup(jsonLookupCaptor.capture());

        assertLookupsEquals(expectedJsonLookup, jsonLookupCaptor.getValue());

        assertEquals(1, output.getLookupProcessorOutputs().size());
        assertEquals(1, output.getLookupProcessorOutputs().get(CLASS_NAME).size());
        assertEquals(expectedLookup, output.getLookupProcessorOutputs().get(CLASS_NAME).get(0));
    }

    @Test
    public void shouldNotAddLookupIfItWasPreviouslyAdded() throws Exception {

        when(bundle.getResource(MDS_LOOKUPS_JSON)).thenReturn(VALID_JSON);
        when(jsonLookupService.exists(CLASS_NAME, ORIGIN_LOOKUP_NAME)).thenReturn(true);

        editableLookupsLoader.addEditableLookups(output, bundle);

        verify(jsonLookupService, times(1)).exists(matches(CLASS_NAME), matches(ORIGIN_LOOKUP_NAME));
        verify(jsonLookupService, times(0)).createJsonLookup(any(JsonLookupDto.class));

        assertEquals(0, output.getLookupProcessorOutputs().size());
    }

    @Test
    public void shouldNotAddLookupIfJsonIsEmpty() {
        when(bundle.getResource(MDS_LOOKUPS_JSON)).thenReturn(EMPTY_JSON);

        editableLookupsLoader.addEditableLookups(output, bundle);

        verify(jsonLookupService, times(0)).exists(anyString(), anyString());
        verify(jsonLookupService, times(0)).createJsonLookup(any(JsonLookupDto.class));

        assertEquals(0, output.getLookupProcessorOutputs().size());
    }

    @Test
    public void shouldNotAddLookupIfThereIsNoJson() {
        when(bundle.getResource(MDS_LOOKUPS_JSON)).thenReturn(null);

        editableLookupsLoader.addEditableLookups(output, bundle);

        verify(jsonLookupService, times(0)).exists(anyString(), anyString());
        verify(jsonLookupService, times(0)).createJsonLookup(any(JsonLookupDto.class));

        assertEquals(0, output.getLookupProcessorOutputs().size());
    }

    @Test(expected = MalformedLookupsJsonException.class)
    public void shouldFailToAddLookupIfJsonIsMalformed() {

        when(bundle.getResource(MDS_LOOKUPS_JSON)).thenReturn(INVALID_JSON);

        editableLookupsLoader.addEditableLookups(output, bundle);
    }

    private LookupDto loadLookup() throws Exception {
        try (InputStream stream = VALID_JSON.openStream()) {
            return GSON.fromJson(IOUtils.toString(stream), EntityLookups[].class)[0].getLookups().get(0);
        }
    }

    private void assertLookupsEquals(JsonLookupDto expectedLookup, JsonLookupDto actual) {
        assertEquals(expectedLookup.getEntityClassName(), actual.getEntityClassName());
        assertEquals(expectedLookup.getOriginLookupName(), actual.getOriginLookupName());
    }

    private void prepareOutput() {
        EntityProcessorOutput entityProcessorOutput = new EntityProcessorOutput();
        entityProcessorOutput.setEntityProcessingResult(prepareEntityDto());

        List<EntityProcessorOutput> entityProcessorOutputs = new ArrayList<>();
        entityProcessorOutputs.add(entityProcessorOutput);

        output = new MDSProcessorOutput(entityProcessorOutputs, new HashMap<>(), bundle);
    }

    private EntityDto prepareEntityDto() {
        EntityDto entity = new EntityDto();
        entity.setClassName(CLASS_NAME);
        return entity;
    }

}
