package org.motechproject.server.web.dto;

import java.util.Arrays;

/**
 * Represents an icon of a bundle. It will be displayed next to module name in the Manage Modules Section in the Admin panel.
 */
public class BundleIcon {
    public static final String[] ICON_LOCATIONS = new String[] {"icon.gif", "icon.jpg", "icon.png"};

    private byte[] icon;
    private String mime;

    /**
     * Constructor.
     *
     * @param icon  the icon to be stored as a byte array
     * @param mime  the mime type of an icon
     */
    public BundleIcon(byte[] icon, String mime) {
        this.icon = Arrays.copyOf(icon, icon.length);
        this.mime = mime;
    }

    public byte[] getIcon() {
        return icon.clone();
    }

    public String getMime() {
        return mime;
    }

    /**
     * Returns size of the icon.
     *
     * @return  the size of stored icon (in bytes)
     */
    public int getContentLength() {
        return (icon == null ? 0 : icon.length);
    }
}
