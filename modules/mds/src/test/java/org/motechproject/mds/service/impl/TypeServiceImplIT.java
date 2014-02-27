package org.motechproject.mds.service.impl;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

}
