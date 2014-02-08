package org.motechproject.mds.service.impl.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.repository.AllEntityDrafts;
import org.motechproject.mds.repository.AllEntityMappings;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.constants.Constants.Packages;

@RunWith(MockitoJUnitRunner.class)
public class EntityServiceImplTest {
    private static final String CLASS_NAME = String.format("%s.Sample", Packages.ENTITY);

    @Mock
    private AllEntityMappings allEntityMappings;

    @Mock
    private AllEntityDrafts allEntityDrafts;

    @Mock
    private MdsJDOEnhancer enhancer;

    @Mock
    private EntityDto entityDto;

    @Mock
    private Entity entity;

    @InjectMocks
    private EntityServiceImpl entityService = new EntityServiceImpl();

    @Test(expected = EntityAlreadyExistException.class)
    public void shouldNotCreateTwiceSameEntity() throws Exception {
        when(entityDto.getClassName()).thenReturn(CLASS_NAME);
        when(allEntityMappings.containsEntity(CLASS_NAME)).thenReturn(true);

        entityService.createEntity(entityDto);
    }

    @Test
    public void shouldDeleteDraftsAndEntities() {
        when(allEntityMappings.getEntityById(1L)).thenReturn(entity);

        entityService.deleteEntity(1L);

        verify(allEntityDrafts).deleteAllDraftsForEntity(entity);
        verify(allEntityMappings).delete(entity);
    }
}
