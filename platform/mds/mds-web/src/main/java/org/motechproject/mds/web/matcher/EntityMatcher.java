package org.motechproject.mds.web.matcher;

import org.motechproject.mds.dto.EntityDto;

import java.util.List;

import static org.apache.commons.lang.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>EntityMatcher</code> checks if the entity name, module or namespace matches
 * the given term.
 */
public class EntityMatcher extends MdsMatcher<EntityDto> {
    private static final int MAX_NUMBER_OF_TERMS = 3;

    public EntityMatcher(final String term) {
        super(EntityDto.class, term);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean match(EntityDto obj) {
        String name = defaultIfBlank(obj.getName(), "");
        String module = defaultIfBlank(obj.getModule(), "");
        String namespace = defaultIfBlank(obj.getNamespace(), "");
        List<String> terms = getTerms(MAX_NUMBER_OF_TERMS);
        Integer count = 0;

        if (isNotBlank(getTerm())) {
            for (String t : terms) {
                if (containsIgnoreCase(name, t) || containsIgnoreCase(module, t)
                        || containsIgnoreCase(namespace, t)) {
                    ++count;
                }
            }
        }

        return isBlank(getTerm()) || count >= terms.size();
    }

}
