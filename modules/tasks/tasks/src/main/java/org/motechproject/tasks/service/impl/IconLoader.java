package org.motechproject.tasks.service.impl;

import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.server.api.BundleIcon;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Component
public class IconLoader {
    public  BundleIcon load(URL iconURL) {
        InputStream is = null;

        try {
            URLConnection urlConn = iconURL.openConnection();
            is = urlConn.getInputStream();

            String mime = urlConn.getContentType();
            byte[] image = IOUtils.toByteArray(is);

            return new BundleIcon(image, mime);
        } catch (IOException e) {
            throw new MotechException("Error loading icon", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}