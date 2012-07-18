package org.motechproject.admin.bundles;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BundleIconTest {

    private static final byte[] ICON = new byte[] { 1, 2, 3};
    private static final String MIME = "image/gif";

    @Test
    public void testBundleIcon() {
        BundleIcon bundleIcon = new BundleIcon(ICON, MIME);

        assertArrayEquals(ICON, bundleIcon.getIcon());
        assertEquals(MIME, bundleIcon.getMime());
        assertEquals(bundleIcon.getContentLength(), ICON.length);
    }

}
