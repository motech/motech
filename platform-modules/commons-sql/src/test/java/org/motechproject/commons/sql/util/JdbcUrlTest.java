package org.motechproject.commons.sql.util;

import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class JdbcUrlTest {

    @Test
    public void shouldParseJdbcUrl() throws URISyntaxException {
        JdbcUrl jdbcUrl = new JdbcUrl("jdbc:mysql://localhost:3306/name_of_db?useSSL=true&requireSSL=true");
        assertEquals("name_of_db", jdbcUrl.getDbName());
        assertEquals("jdbc:mysql://localhost:3306?useSSL=true&requireSSL=true", jdbcUrl.getUrlForDbServer());
    }

    @Test
    public void shouldParsePsqlJdbc() throws URISyntaxException {
        JdbcUrl jdbcUrl = new JdbcUrl("jdbc:postgresql://localhost/test?user=fred&password=secret&ssl=true");
        assertEquals("test", jdbcUrl.getDbName());
        assertEquals("jdbc:postgresql://localhost?user=fred&password=secret&ssl=true", jdbcUrl.getUrlForDbServer());
    }

    @Test
    public void shouldParseUrlNoPortNoParams() throws URISyntaxException {
        JdbcUrl jdbcUrl = new JdbcUrl("jdbc:postgresql://localhost/test");
        assertEquals("test", jdbcUrl.getDbName());
        assertEquals("jdbc:postgresql://localhost", jdbcUrl.getUrlForDbServer());
    }
}
