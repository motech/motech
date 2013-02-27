package org.motechproject.tasks.service.impl;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.service.ChannelService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChannelServiceImplTest {
    private static final String MODULE_NAME = "test";
    private static final String VERSION = "0.16";

    @Mock
    AllChannels allChannels;

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundle;

    @Mock
    InputStream inputStream;

    @Mock
    MotechJsonReader motechJsonReader;

    ChannelService channelService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        channelService = new ChannelServiceImpl(allChannels, motechJsonReader);
    }

    @Test
    public void shouldRegisterChannel() {
        Type type = new TypeToken<Channel>() { }.getType();
        Channel channel = new Channel();

        when(motechJsonReader.readFromStream(inputStream, type)).thenReturn(channel);

        channelService.registerChannel(inputStream);

        verify(motechJsonReader).readFromStream(inputStream, type);
        verify(allChannels).addOrUpdate(channel);
    }

    @Test
    public void shouldGetAllChannels() {
        List<Channel> expected = new ArrayList<>();
        expected.add(new Channel());
        expected.add(new Channel());

        when(allChannels.getAll()).thenReturn(expected);

        List<Channel> actual = channelService.getAllChannels();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetChannelByChannelInfo() {
        String displayName = "Test";
        String moduleName = "test-1";

        Channel expected = new Channel();
        expected.setDisplayName(displayName);
        expected.setModuleName(moduleName);
        expected.setModuleVersion(VERSION);

        when(allChannels.byChannelInfo(displayName, moduleName, VERSION)).thenReturn(expected);

        Channel actual = channelService.getChannel(displayName, moduleName, VERSION);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenBundleContextNotSet() throws IOException {
        whenGetChannelIcon(getDefaultIconUrl());

        channelService.getChannelIcon(MODULE_NAME, VERSION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenBundleContextNotContainBundles() {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);
        when(bundleContext.getBundles()).thenReturn(new Bundle[]{});

        channelService.getChannelIcon(MODULE_NAME, VERSION);

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenBundleNotFound() throws IOException {
        String fakeModuleName = "testing";

        whenGetChannelIcon(getDefaultIconUrl());

        channelService.getChannelIcon(fakeModuleName, VERSION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenVersionNotMatch() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);

        whenGetChannelIcon(getDefaultIconUrl());

        channelService.getChannelIcon(MODULE_NAME, "0.15");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSymbolicNameNotMatch() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);

        whenGetChannelIcon(getDefaultIconUrl());

        channelService.getChannelIcon("faceModule", VERSION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSymbolicNameAndVersionNotMatch() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);

        whenGetChannelIcon(getDefaultIconUrl());

        channelService.getChannelIcon("faceModule", "0.15");
    }

    @Test
    public void shouldGetChannelIcon() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);
        byte[] image = readDefaultIcon();

        whenGetChannelIcon(getDefaultIconUrl());

        BundleIcon bundleIcon = channelService.getChannelIcon(MODULE_NAME, VERSION);

        assertNotNull(bundleIcon);
        assertEquals("image/png", bundleIcon.getMime());
        assertArrayEquals(image, bundleIcon.getIcon());
        assertEquals(image.length, bundleIcon.getContentLength());
    }

    @Test
    public void shouldReturnNullWhenBundleNotContainIcon() throws IOException {
        ((ChannelServiceImpl) channelService).setBundleContext(bundleContext);

        whenGetChannelIcon(null);

        BundleIcon bundleIcon = channelService.getChannelIcon(MODULE_NAME, VERSION);

        assertNull(bundleIcon);
    }

    private void whenGetChannelIcon(URL iconUrl) throws IOException {
        when(bundle.getSymbolicName()).thenReturn(String.format("org.motechproject.%s", MODULE_NAME));
        when(bundle.getVersion()).thenReturn(Version.parseVersion(VERSION));
        when(bundle.getResource(BundleIcon.ICON_LOCATIONS[0])).thenReturn(iconUrl);

        when(bundleContext.getBundles()).thenReturn(new Bundle[]{bundle});
    }

    private static byte[] readDefaultIcon() {
        URL url = getDefaultIconUrl();

        try (InputStream is = url.openStream()) {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL getDefaultIconUrl() {
        return ChannelServiceImplTest.class.getResource("/bundle_icon.png");
    }
}
