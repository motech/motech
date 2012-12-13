package org.motechproject.server.api;

import java.util.Arrays;

public class BundleIcon {
    public static final String[] ICON_LOCATIONS = new String[] {"icon.gif", "icon.jpg", "icon.png"};

    private byte[] icon;
    private String mime;

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

    public int getContentLength() {
        return (icon == null ? 0 : icon.length);
    }
}
