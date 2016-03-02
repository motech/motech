package org.motechproject.mds.it.service;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.Test;
import org.motechproject.mds.it.BaseIT;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.domain.Relationship;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class TypeServiceImplContextIT extends BaseIT {
    private static final int START_NUMBER_OF_TYPES = 24;

    @Autowired
    private TypeService typeService;

    @Test
    public void shouldRetrieveTypes() {
        List<TypeDto> types = typeService.getAllTypes();

        assertThat(types.size(), Is.is(START_NUMBER_OF_TYPES));
        assertThat(types, Matchers.hasItem(TypeDto.INTEGER));
        assertThat(types, Matchers.hasItem(TypeDto.BOOLEAN));
    }

    @Test
    public void shouldRetrieveCorrectTypes() {
        testFindType(Boolean.class, Boolean.class);
        testFindType(Integer.class, Integer.class);
        testFindType(Double.class, Double.class);
        testFindType(Collection.class, Collection.class);
        testFindType(Date.class, Date.class);
        testFindType(DateTime.class, DateTime.class);
        testFindType(LocalDateTime.class, LocalDateTime.class);
        testFindType(String.class, String.class);
        testFindType(Map.class, Map.class);
        testFindType(Period.class, Period.class);
        testFindType(Locale.class, Locale.class);
        testFindType(Byte[].class, Byte[].class);
        testFindType(Long.class, Long.class);
        testFindType(LocalDate.class, LocalDate.class);
        testFindType(java.time.LocalDate.class, java.time.LocalDate.class);
        testFindType(Relationship.class, Relationship.class);
        testFindType(OneToManyRelationship.class, OneToManyRelationship.class);
        testFindType(OneToOneRelationship.class, OneToOneRelationship.class);

        //test primitives
        testFindType(boolean.class, Boolean.class);
        testFindType(int.class, Integer.class);
        testFindType(double.class, Double.class);
        testFindType(long.class, Long.class);
        testFindType(short.class, Short.class);
        testFindType(float.class, Float.class);
        testFindType(char.class, Character.class);
    }

    private void testFindType(Class<?> request, Class<?> expected) {
        TypeDto type = typeService.findType(request);
        assertNotNull(type);
        assertEquals(expected.getName(), type.getTypeClass());
    }
}
