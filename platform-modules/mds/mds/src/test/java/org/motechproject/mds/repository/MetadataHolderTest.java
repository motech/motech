package org.motechproject.mds.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.metadata.JDOMetadata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetadataHolderTest {

    @InjectMocks
    private MetadataHolder metadataHolder = new MetadataHolder();

    @Mock
    private PersistenceManagerFactory pmf;

    @Test
    public void shouldReturnAndReloadMetadata() {
        JDOMetadata jdoMetadata = mock(JDOMetadata.class);
        when(pmf.newMetadata()).thenReturn(jdoMetadata);

        // constructs initial metadata, then reloads
        assertEquals(jdoMetadata, metadataHolder.getJdoMetadata());
        assertEquals(jdoMetadata, metadataHolder.reloadMetadata());
        verify(pmf, times(2)).newMetadata();

        // retrieves existing metadata
        assertEquals(jdoMetadata, metadataHolder.getJdoMetadata());
        verifyNoMoreInteractions(pmf);
    }
}
