package org.motechproject.cmslite.api.service;

import org.junit.Test;
import org.motechproject.cmslite.api.dao.AllResources;
import org.motechproject.cmslite.api.model.Resource;
import org.motechproject.cmslite.api.model.ResourceNotFoundException;
import org.motechproject.cmslite.api.model.ResourceQuery;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class CMSLiteServiceTest {

    @Test
    public void shouldReturnInputStreamToContentIfContentExists() throws IOException, ResourceNotFoundException {
        ResourceQuery query = new ResourceQuery("name", "language");
        AllResources mockResources = mock(AllResources.class);
        CMSLiteService cmsLiteService = new CMSLiteServiceImpl(mockResources);
        Resource resource = new Resource();
        InputStream inputStreamToResource = mock(InputStream.class);

        resource.setInputStream(inputStreamToResource);
        when(mockResources.getResource(query)).thenReturn(resource);

        InputStream content = cmsLiteService.getContent(query);

        verify(mockResources).getResource(query);
        assertEquals(inputStreamToResource, content);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowExceptionIfContentDoesNotExist() throws ResourceNotFoundException {
        ResourceQuery query = new ResourceQuery("test1", "language");
        AllResources mockResources = mock(AllResources.class);
        CMSLiteService cmsLiteService = new CMSLiteServiceImpl(mockResources);

        when(mockResources.getResource(query)).thenReturn(null);

        cmsLiteService.getContent(query);

        fail("Should have thrown ResourceNotFoundException when query is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenQueryIsNull() throws ResourceNotFoundException {
        AllResources mockResources = mock(AllResources.class);
        CMSLiteService cmsLiteService = new CMSLiteServiceImpl(mockResources);

        cmsLiteService.getContent(null);
        verify(mockResources,never()).getResource(null);

        fail("Should have thrown IllegalArgumentException when query is null");
    }

}
