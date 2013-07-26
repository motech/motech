package org.motechproject.tasks.service.impl.it;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.tasks.service.impl.IconLoader;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertArrayEquals;

public class IconLoaderTest {

    private static final String DEFAULT_ICON_PATH = "/bundle_icon.png";

    @Test
    public void shouldLoadIconFromURL() throws IOException {
        ClassPathResource icon = new ClassPathResource(DEFAULT_ICON_PATH);
        BundleIcon bundleIcon = new IconLoader().load(icon.getURL());
        byte[] expected = readDefaultIcon();
        assertArrayEquals(expected, bundleIcon.getIcon());
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
        return IconLoaderTest.class.getResource(DEFAULT_ICON_PATH);
    }

}
