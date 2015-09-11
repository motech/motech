package org.motechproject.mds.annotations.internal;

import junit.framework.Assert;
import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestIgnoreProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    @Mock
    private AnnotationProcessingContext context;

    private RestIgnoreProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new RestIgnoreProcessor();
    }

    @Test
    public void shouldProperlyDiscoverRestIgnoredFields() throws Exception {
        RestOptionsDto restOptionsDto = new RestOptionsDto();
        List<FieldDto> fields = mockEntityFields();

        processor.setClazz(AnotherSample.class);
        processor.setFields(fields);
        processor.setRestOptions(restOptionsDto);
        processor.execute(bundle, context);

        // entity fields + auto generated fields
        Assert.assertEquals(7, processor.getProcessingResult().getFieldNames().size());

        // @RestIgnore annotated
        Assert.assertFalse(processor.getProcessingResult().containsField("modificationDate"));
        Assert.assertFalse(processor.getProcessingResult().containsField("restIgnoreBoolean"));

        // not annotated, regular fields
        Assert.assertTrue(processor.getProcessingResult().containsField("anotherInt"));
        Assert.assertTrue(processor.getProcessingResult().containsField("anotherString"));
    }

    private List<FieldDto> mockEntityFields() {
        List<FieldDto> fields = new ArrayList<>();
        Map<String, Long> nameIdMapping = new HashMap<>();
        List<String> fieldNames = new ArrayList<>(Arrays.asList(Constants.Util.GENERATED_FIELD_NAMES));
        fieldNames.addAll(Arrays.asList("anotherInt", "anotherString", "restIgnoredBoolean"));
        long i = 0;
        for (String fieldName : fieldNames) {
            long fieldId = i++;
            nameIdMapping.put(fieldName, fieldId);
            FieldDto fieldDto = mock(FieldDto.class);
            FieldBasicDto fieldBasicDto = mock(FieldBasicDto.class);
            when(fieldDto.getId()).thenReturn(fieldId);
            when(fieldDto.getBasic()).thenReturn(fieldBasicDto);
            when(fieldBasicDto.getName()).thenReturn(fieldName);
            fields.add(fieldDto);
        }
        return fields;
    }
}
