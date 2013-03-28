package org.motechproject.commons.couchdb.lucene.query;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.api.Range;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static junit.framework.Assert.assertEquals;

public class CouchDbLuceneQueryTest {

    @Test
    public void shouldCreateValidQuery() {
        // Format: yyyy-MM-dd'T'HH:mm:ss.SSSZ
        String dateString = "2013-03-20T10:20:30.400+0000";
        DateTime date = DateTime.parse(dateString);
        Range<DateTime> range = new Range<>(date, date);
        Set<String> multi = new TreeSet<>();
        multi.add("x");
        multi.add("y");
        multi.add("z");

        StringBuilder query = new CouchDbLuceneQuery()
                .with("name", "foo")
                .withInt("age", 23)
                .withDate("dob", date)
                .withAny("multi", multi)
                .withDateRange("range", range)
                .build();
        assertEquals("name:foo AND age<int>:23 AND dob<date>:" + dateString +
                " AND (multi:x OR multi:y OR multi:z)" +
                " AND range<date>:[" + dateString + " TO " + dateString + "]", query.toString());
    }

    @Test
    public void shouldCreateValidQueryIgnoringBlank() {
        StringBuilder query = new CouchDbLuceneQuery()
                .with("name", "foo")
                .with(null, "foo")
                .withInt(null, 23)
                .withDate("dob", null)
                .withAny(null, new HashSet<String>())
                .withDateRange("range", null)
                .build();
        assertEquals("name:foo", query.toString());
    }
}
