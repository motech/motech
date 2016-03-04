package org.motechproject.mds.annotations.internal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.service.EntityService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchemaComparatorTest {

    private static final Long ENTITY_ID = 1L;

    @Mock
    private EntityService entityService;
    private SchemaComparator schemaComparator;
    private List<LookupDto> entityLookups;
    private List<LookupDto> newLookups;

    @Before
    public void setUp() {
        schemaComparator = new SchemaComparator();
        schemaComparator.setEntityService(entityService);
    }

    @Test
    public void shouldCompareLookupsWithDifferentSize() {
        LookupDto lookupDto = new LookupDto();
        lookupDto.setReadOnly(true);

        entityLookups = asList(lookupDto);
        newLookups = asList(lookupDto, lookupDto);

        when(entityService.getEntityLookups(ENTITY_ID)).thenReturn(entityLookups);

        boolean lookupsDiffer = schemaComparator.lookupsDiffer(ENTITY_ID, newLookups);
        assertTrue(lookupsDiffer);
    }

    @Test
    public void shouldCompareLookupsWithDifferentNames() {
        entityLookups = asList(new LookupDto("lookupName", true, true, null, true));
        newLookups = asList(new LookupDto("differentLookupName", true, true, null, true));

        when(entityService.getEntityLookups(ENTITY_ID)).thenReturn(entityLookups);

        boolean lookupsDiffer = schemaComparator.lookupsDiffer(ENTITY_ID, newLookups);
        assertTrue(lookupsDiffer);
    }

    @Test
    public void shouldCompareLookupsWithDifferentFlags() {
        entityLookups = asList(new LookupDto("lookupName", true, true, null, true));
        newLookups = asList(new LookupDto("lookupName", false, false, null, true));

        when(entityService.getEntityLookups(ENTITY_ID)).thenReturn(entityLookups);

        boolean lookupsDiffer = schemaComparator.lookupsDiffer(ENTITY_ID, newLookups);
        assertTrue(lookupsDiffer);
    }

    @Test
    public void shouldCompareLookupsWithDifferentFields() {
        LookupDto lookupDto = new LookupDto("lookupName", true, true, new ArrayList<LookupFieldDto>(), true, "methodName", new ArrayList<String>());
        LookupFieldDto lookupFieldDto = new LookupFieldDto(1L, "name", LookupFieldType.RANGE, "customOperator", true, "relatedName");
        lookupDto.setLookupFields(asList(lookupFieldDto));

        LookupDto lookupDto2 = new LookupDto("lookupName", true, true, new ArrayList<LookupFieldDto>(), true, "methodName", new ArrayList<String>());
        LookupFieldDto lookupFieldDto2 = new LookupFieldDto(1L, "name", LookupFieldType.RANGE, "customOperator", true, "differentRelatedName");
        lookupDto2.setLookupFields(asList(lookupFieldDto2));

        entityLookups = asList(lookupDto);
        newLookups = asList(lookupDto2);

        when(entityService.getEntityLookups(ENTITY_ID)).thenReturn(entityLookups);

        boolean lookupsDiffer = schemaComparator.lookupsDiffer(ENTITY_ID, newLookups);
        assertTrue(lookupsDiffer);
    }

    @Test
    public void shouldCompareTheSameLookups() {
        LookupDto lookupDto = new LookupDto("lookupName", true, true, new ArrayList<LookupFieldDto>(), true, "methodName", new ArrayList<String>());

        entityLookups = asList(lookupDto);
        newLookups = asList(lookupDto);

        when(entityService.getEntityLookups(ENTITY_ID)).thenReturn(entityLookups);

        boolean lookupsDiffer = schemaComparator.lookupsDiffer(ENTITY_ID, newLookups);
        assertFalse(lookupsDiffer);
    }
    @Test
    public void shouldCompareOnlyReadOnlyLookups() {
        LookupDto lookupDto = new LookupDto("lookupName", true, true, new ArrayList<LookupFieldDto>(), true, "methodName", new ArrayList<String>());
        LookupDto lookupDto2 = new LookupDto();
        lookupDto2.setReadOnly(false);

        entityLookups = asList(lookupDto, lookupDto2);
        newLookups = asList(lookupDto);

        when(entityService.getEntityLookups(ENTITY_ID)).thenReturn(entityLookups);

        boolean lookupsDiffer = schemaComparator.lookupsDiffer(ENTITY_ID, newLookups);
        assertFalse(lookupsDiffer);
    }
}
