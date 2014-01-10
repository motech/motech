package org.motechproject.mds.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.repository.AllEntityMappings;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityServiceImplTest {
    private static final String SIMPLE_NAME = "Sample";

    @Mock
    private AllEntityMappings allEntityMappings;

    @Mock
    private EntityDto entityDto;

    private EntityServiceImpl entityService;

    @Before
    public void setUp() throws Exception {
        entityService = new EntityServiceImpl();
        entityService.setAllEntityMappings(allEntityMappings);

    }

    @Test(expected = EntityReadOnlyException.class)
    public void shouldNotCreateEntityIfDtoIsReadOnly() throws Exception {
        when(entityDto.isReadOnly()).thenReturn(true);

        entityService.createEntity(entityDto);
    }

    @Test(expected = EntityAlreadyExistException.class)
    public void shouldNotCreateTwiceSameEntity() throws Exception {
        when(entityDto.isReadOnly()).thenReturn(false);
        when(entityDto.getName()).thenReturn(SIMPLE_NAME);

        when(allEntityMappings.containsEntity(SIMPLE_NAME)).thenReturn(true);

        entityService.createEntity(entityDto);
    }
}
