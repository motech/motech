package org.motechproject.server.config.domain;

import org.apache.commons.lang.StringUtils;

public class MotechURL {

    public static final String URL_PATTERN = "^\\w+://.*";
    private String url;

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
