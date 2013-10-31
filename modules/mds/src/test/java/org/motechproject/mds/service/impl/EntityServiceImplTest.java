package org.motechproject.mds.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.exception.EntityAlreadyExistException;
import org.motechproject.mds.exception.MDSValidationException;
import org.motechproject.mds.repository.EntityRepository;
import org.motechproject.mds.service.EntityService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityServiceImplTest {
    @Mock
    private EntityRepository entityRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private EntityService entityService;

    @Before
    public void setUp() {
        entityService = new EntityServiceImpl(entityRepository);
    }

    @Test
    public void shouldCreateEntity() {
        String entityName = "name";
        EntityDto entityDto = mock(EntityDto.class);
        when(entityDto.getId()).thenReturn("id");
        when(entityDto.getName()).thenReturn(entityName);
        when(entityDto.getModule()).thenReturn("module");
        when(entityRepository.findByName(entityName)).thenReturn(null);

        entityService.create(entityDto);

        InOrder inOrder = inOrder(entityDto, entityRepository);
        inOrder.verify(entityDto).validate();
        inOrder.verify(entityRepository).findByName(entityName);

        ArgumentCaptor<Entity> entityArgumentCaptor = ArgumentCaptor.forClass(Entity.class);
        inOrder.verify(entityRepository).create(entityArgumentCaptor.capture());
        Entity savedEntity = entityArgumentCaptor.getValue();
        assertEquals(entityDto.getName(), savedEntity.getName());
    }

    @Test(expected = MDSValidationException.class)
    public void shouldThrowExceptionIfEntityDtoValidationFails() {
        EntityDto entityDto = mock(EntityDto.class);
        doThrow(new MDSValidationException("validation failure")).when(entityDto).validate();

        entityService.create(entityDto);

        verifyZeroInteractions(entityRepository);
    }

    @Test
    public void shouldNotCreateEntityIfItAlreadyExists() {
        String entityName = "name";
        when(entityRepository.findByName(entityName)).thenReturn(new Entity(entityName, "module"));

        expectedException.expect(EntityAlreadyExistException.class);
        expectedException.expectMessage("key:mds.validation.error.entityAlreadyExist");

        entityService.create(new EntityDto(null, entityName));

        verify(entityRepository).findByName(entityName);
        verifyNoMoreInteractions(entityRepository);
    }
}
