package org.motechproject.server.config.domain;

import org.apache.commons.lang.StringUtils;

/**
 * A MOTECH class representing URL using "protocol://host" pattern.
 */
public class MotechURL {

    public static final String URL_PATTERN = "^\\w+://.*";
    private String url;

    /**
     * Constructor.
     *
     * @param url  the URL to be stored, if it doesn't include protocol, "http://" will be added in front
     */
    public MotechURL(String url) {
        this.url = format(url);
    }

    @Override
    public String toString() {
        return this.url;
    }

    private String format(String urlToFormat) {
        if (urlToFormat == null) {
            return null;
        }

        String trimmedUrl = urlToFormat.trim();

        if (trimmedUrl.isEmpty()) {
            return trimmedUrl;
        }

        if (!trimmedUrl.matches(URL_PATTERN)) {
            return String.format("http://%s", trimmedUrl);
        }
        return trimmedUrl;
    }

    /**
     * Returns host of the stored URL.
     *
     * @return the host of stored URL
     */
    public String getHost() {
        if (StringUtils.isNotBlank(url) && url.matches(URL_PATTERN)) {
            final String protocolDelimiter = "://";
            final int protocolDelimiterLength = protocolDelimiter.length();
            int index = url.indexOf(protocolDelimiter);

            int indexOfBackslash = url.indexOf("/", index + protocolDelimiterLength);
            if (indexOfBackslash == -1) {
                return url.substring(index + protocolDelimiterLength, url.length());
            }
            return url.substring(index + protocolDelimiterLength, indexOfBackslash);
        }
        return null;
    }
}
