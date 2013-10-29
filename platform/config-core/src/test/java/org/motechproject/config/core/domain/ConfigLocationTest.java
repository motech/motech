package org.motechproject.config.core.domain;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.motechproject.config.core.MotechConfigurationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ConfigLocationTest {

    @Test
    public void shouldConvertFileLocationToResource() throws IOException {
        ConfigLocation configLocation = new ConfigLocation("/etc/motech/");

        Resource resource = configLocation.toResource();

        assertEquals("file:/etc/motech/", resource.getURL().toString());
    }

    @Test
    public void shouldConvertClasspathLocationToResource() {
        ConfigLocation configLocation = new ConfigLocation("config/");

        Resource resource = configLocation.toResource();

        assertEquals(new ClassPathResource("config/"), resource);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionWhenInvalidConfigLocationIsGiven() throws MalformedURLException {
        ConfigLocation configLocation = new ConfigLocationStub("/location");
        configLocation.toResource();
    }

    @Test
    public void shouldGetFileForGivenFileAccessType() {
        String userHome = System.getProperty("user.home");
        ConfigLocation configLocation = new ConfigLocation(userHome);

        File file = configLocation.getFile("filename", ConfigLocation.FileAccessType.WRITABLE);

        assertNotNull(file);
        assertThat(file.getAbsolutePath(), IsEqual.equalTo(userHome + File.separator + "filename"));
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionWhenFileIsNotWritableWhenAskedForWritableAccessType() throws IOException {
        ConfigLocation configLocation = new ConfigLocation("tmp");
        ConfigLocation configLocationSpy = spy(configLocation);

        Resource configLocationResource = mock(Resource.class);
        doReturn(configLocationResource).when(configLocationSpy).toResource();
        Resource fileResource = mock(Resource.class);
        when(configLocationResource.createRelative("filename")).thenReturn(fileResource);
        File fileMock = mock(File.class);
        when(fileResource.getFile()).thenReturn(fileMock);
        when(fileMock.canWrite()).thenReturn(false);
        when(fileMock.exists()).thenReturn(true);

        configLocationSpy.getFile("filename", ConfigLocation.FileAccessType.WRITABLE);
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionWhenFileAccessCheckThrowsError() throws IOException {
        ConfigLocation configLocation = new ConfigLocation("tmp");
        ConfigLocation configLocationSpy = spy(configLocation);

        Resource configLocationResource = mock(Resource.class);
        Resource fileResource = mock(Resource.class);
        when(configLocationResource.createRelative("filename")).thenReturn(fileResource);
        doReturn(configLocationResource).when(configLocationSpy).toResource();
        doThrow(new IOException()).when(fileResource).getFile();

        configLocationSpy.getFile("filename", ConfigLocation.FileAccessType.WRITABLE);
    }

    @Test
    public void shouldGetFileRelativeToConfigLocationGivenAnAccessType() throws IOException {
        String configDir = "config/";
        ConfigLocation configLocation = new ConfigLocation(configDir);

        File file = configLocation.getFile("test.properties", ConfigLocation.FileAccessType.READABLE);

        assertNotNull(file);
        assertThat(new ClassPathResource(configDir + "test.properties").getURL().getFile(), IsEqual.equalTo(file.getAbsolutePath()));
    }

    @Test
    public void shouldAppendFileSeparatorIfGivenLocationDoesNotEndWithOne() {
        String userHome = System.getProperty("user.home");
        ConfigLocation configLocation = new ConfigLocation(userHome);
        assertThat(configLocation, IsEqual.equalTo(new ConfigLocation(userHome + File.separator)));
    }

    private class ConfigLocationStub extends ConfigLocation {
        public ConfigLocationStub(String configLocation) {
            super(configLocation);
        }

        @Override
        UrlResource getUrlResource() throws MalformedURLException {
            throw new MalformedURLException("Malformed");
        }
    }
}
