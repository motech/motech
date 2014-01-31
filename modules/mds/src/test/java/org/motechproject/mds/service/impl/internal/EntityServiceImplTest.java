package org.motechproject.mds.service.impl.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.repository.AllEntityDrafts;
import org.motechproject.mds.repository.AllEntityMappings;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityServiceImplTest {
    private static final String SIMPLE_NAME = "Sample";

    @Mock
    private AllEntityMappings allEntityMappings;

    @Mock
    private AllEntityDrafts allEntityDrafts;

    @Mock
    private MdsJDOEnhancer enhancer;

    @Mock
    private EntityDto entityDto;

    @Mock
    private EntityMapping entityMapping;

    @InjectMocks
    private EntityServiceImpl entityService = new EntityServiceImpl();

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

    @Test
    public void shouldDeleteDraftsAndEntities() {
        when(allEntityMappings.getEntityById(1L)).thenReturn(entityMapping);

        entityService.deleteEntity(1L);

        verify(allEntityDrafts).deleteAllDraftsForEntity(entityMapping);
        verify(allEntityMappings).delete(entityMapping);
    }
}
