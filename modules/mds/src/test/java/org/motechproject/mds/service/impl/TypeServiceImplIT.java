package org.motechproject.mds.service.impl;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class TypeServiceImplIT extends BaseIT {
    private static final int START_NUMBER_OF_TYPES = 9;

    @Autowired
    private TypeService typeService;

    @Before
    public void setUp() {
        clearDB();
    }

    @After
    public void tearDown() {
        clearDB();
    }

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
        testFindType(List.class, List.class);
        testFindType(Date.class, Date.class);
        testFindType(DateTime.class, DateTime.class);
        testFindType(Long.class, Long.class);
        testFindType(String.class, String.class);
        //test primitives
        testFindType(boolean.class, Boolean.class);
        testFindType(int.class, Integer.class);
        testFindType(double.class, Double.class);
    }

    private void testFindType(Class<?> request, Class<?> expected) {
        TypeDto type = typeService.findType(request);
        assertNotNull(type);
        assertEquals(expected.getName(), type.getTypeClass());
    }
}
