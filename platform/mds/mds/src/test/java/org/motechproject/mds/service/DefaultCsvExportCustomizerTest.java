package org.motechproject.mds.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCsvExportCustomizerTest {

    private static final long ENTITY_ID = 4;
    private static final long ANOTHER_ENTITY_ID = 6;

    private static final String EXPECTED_COLLECTION_RESULT = ENTITY_ID + "," + ANOTHER_ENTITY_ID;

    private DefaultCsvExportCustomizer exportCustomizer = new DefaultCsvExportCustomizer();

    @Mock
    private EntityDto entityDto;

    @Mock
    private FieldDto field;

    @Mock
    private EntityDto anotherEntityDto;

    @Before
    public void setUp() {
        when(entityDto.getId()).thenReturn(ENTITY_ID);
        when(anotherEntityDto.getId()).thenReturn(ANOTHER_ENTITY_ID);
    }

    @Test
    public void shouldImportInstancesByClassName() {
        ArrayList<EntityDto> entityDtos = new ArrayList<>();
        entityDtos.add(entityDto);
        entityDtos.add(anotherEntityDto);

        assertEquals("", exportCustomizer.formatField(field, null));
        assertEquals("", exportCustomizer.formatField(field, new ArrayList<>()));
        assertEquals(String.valueOf(ENTITY_ID), exportCustomizer.formatField(field, entityDto));
        assertEquals(EXPECTED_COLLECTION_RESULT, exportCustomizer.formatField(field, entityDtos));
    }
}
