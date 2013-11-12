package org.motechproject.mds.web.matcher;

import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>MdsMatcher</code> is a basic generic wrapper for all matchers inside mds module.
 */
public abstract class MdsMatcher<T> implements Predicate {
    private static final String DEFAULT_SPLIT_CHAR = ",";

    private String term;
    private Class<T> clazz;

    protected MdsMatcher(Class<T> clazz) {
        this(clazz, "");
    }

    protected MdsMatcher(final Class<T> clazz, final String term) {
        this.clazz = clazz;
        this.term = term;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(Object object) {
        return clazz.isAssignableFrom(object.getClass()) && match(clazz.cast(object));
    }

    protected abstract boolean match(T obj);

    protected String getFirstTerm() {
        return getFirstTerm(DEFAULT_SPLIT_CHAR);
    }

    protected String getFirstTerm(final String splitChar) {
        List<String> terms = getTerms(1, splitChar);
        return terms.isEmpty() ? EMPTY : terms.get(0);
    }

    protected List<String> getTerms(final int numberOfTerms) {
        return getTerms(numberOfTerms, DEFAULT_SPLIT_CHAR);
    }

    protected List<String> getTerms(final int numberOfTerms, final String splitChar) {
        List<String> terms = new ArrayList<>();

        for (String t : term.split(splitChar)) {
            if (isNotBlank(t)) {
                terms.add(t.trim());
            }

            if (terms.size() == numberOfTerms) {
                break;
            }
        }

        return terms;
    }

    protected String getTerm() {
        return term;
    }
}
