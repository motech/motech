package org.motechproject.mds.annotations.internal;

import junit.framework.Assert;
import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestIgnoreProcessorTest {

    @Spy
    private MockBundle bundle = new MockBundle();

    @Spy
    private RestOptionsDto restOptionsDto = new RestOptionsDto();

    @Mock
    private EntityService entityService;

    @Mock
    private AdvancedSettingsDto advancedSettingsDto;

    @Mock
    private EntityDto entity;

    @Captor
    private ArgumentCaptor<RestOptionsDto> restOptionsDtoCaptor;

    private RestIgnoreProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new RestIgnoreProcessor();
        processor.setEntityService(entityService);
    }

    @Test
    public void shouldIgnoreFields() throws Exception {
        when(entity.getId()).thenReturn(1L);
        when(entityService.getAdvancedSettings(eq(1L), eq(true))).thenReturn(advancedSettingsDto);
        when(advancedSettingsDto.getRestOptions()).thenReturn(restOptionsDto);

        Map<String, Long> nameIdMapping = mockEntityFields();

        processor.setClazz(AnotherSample.class);
        processor.setEntity(entity);
        processor.execute(bundle);

        verify(entityService).updateRestOptions(eq(1L), restOptionsDtoCaptor.capture());
        RestOptionsDto restOptions = restOptionsDtoCaptor.getValue();

        Assert.assertEquals(7, restOptions.getFieldIds().size());
        Assert.assertFalse(restOptions.getFieldIds().contains(nameIdMapping.get("modificationDate")));
        Assert.assertFalse(restOptions.getFieldIds().contains(nameIdMapping.get("restIgnoreBoolean")));
    }

    private Map<String, Long> mockEntityFields() {
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
        when(entityService.getEntityFields(1L)).thenReturn(fields);
        return nameIdMapping;
    }
}
