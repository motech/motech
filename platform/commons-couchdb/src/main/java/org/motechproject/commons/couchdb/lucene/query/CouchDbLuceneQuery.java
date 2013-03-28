package org.motechproject.commons.couchdb.lucene.query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;

import java.util.Set;

import static java.lang.String.format;

public class CouchDbLuceneQuery {

    private static final String AND = " AND ";
    private static final String OR = " OR ";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private StringBuilder query = new StringBuilder();

    public CouchDbLuceneQuery with(String field, String value) {
        if (isValid(field, value)) {
            appendToQuery(format("%s:%s", field, value));
        }
        return this;
    }

    public CouchDbLuceneQuery withInt(String field, int value) {
        return withField(field, "int", String.valueOf(value));
    }

    public CouchDbLuceneQuery withDate(String field, DateTime value) {
        if (value != null) {
            withField(field, "date", value.toString(DATE_TIME_FORMAT));
        }
        return this;
    }

    public CouchDbLuceneQuery withField(String field, String type, String value) {
        if (isValid(field, type, value)) {
            appendToQuery(format("%s<%s>:%s", field, type, value));
        }
        return this;
    }

    public CouchDbLuceneQuery withAny(String field, Set<String> values) {
        if (isValid(field) && !CollectionUtils.isEmpty(values)) {
            StringBuilder orCondition = new StringBuilder("(");
            int i = 0;
            for (String value : values) {
                if (i > 0) {
                    orCondition.append(OR);
                }
                orCondition.append(format("%s:", field)).append(value);
                i++;
            }
            orCondition.append(")");
            appendToQuery(orCondition.toString());
        }
        return this;
    }

    public CouchDbLuceneQuery withDateRange(String field, Range<DateTime> value) {
        if (value != null) {
            String minValue = value.getMin().toString(DATE_TIME_FORMAT);
            String maxValue = value.getMax().toString(DATE_TIME_FORMAT);
            if (isValid(field, minValue, maxValue)) {
                appendToQuery(format("%s<date>:[%s TO %s]", field, minValue, maxValue));
            }
        }
        return this;
    }

    private boolean isValid(String... params) {
        for (String param : params) {
            if (StringUtils.isBlank(param)) {
                return false;
            }
        }
        return true;
    }

    private void appendToQuery(String condition) {
        if (query.length() > 0) {
            query.append(AND);
        }
        query.append(condition);
    }


    public StringBuilder build() {
        return query;
    }
}
