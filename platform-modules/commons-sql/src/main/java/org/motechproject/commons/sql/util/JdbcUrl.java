package org.motechproject.commons.sql.util;

import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Represents a JDBC URL, allows getting the url or database name parts.
 * Support primarily Postgresql and MySQL.
 */
public class JdbcUrl {

    private final URI uri;

    /**
     * Constructs this from the provided url.
     * @param url the url to constructs this object from
     * @throws URISyntaxException if the url is invalid
     */
    public JdbcUrl(String url) throws URISyntaxException {
        // URI does not contain jdbc:
        String parsedUrl = url.replaceFirst("jdbc:", "");
        uri = new URI(parsedUrl);
    }

    /**
     * @return the name of the database from this url
     */
    public String getDbName() {
        // drop first /
        return uri.getPath().substring(1);
    }

    /**
     * @return get an url for connecting with the server without the database part
     */
    public String getUrlForDbServer() {
        String queryPart = (StringUtils.isBlank(uri.getQuery())) ? "" : '?' + uri.getQuery();
        String portPart = (uri.getPort() == -1) ? "" : ":" + uri.getPort();
        return String.format("jdbc:%s://%s%s%s", uri.getScheme(), uri.getHost(), portPart, queryPart);
    }

    /**
     * @return returns the entire url
     */
    public String getValue() {
        return "jdbc:" + uri.toString();
    }
}
