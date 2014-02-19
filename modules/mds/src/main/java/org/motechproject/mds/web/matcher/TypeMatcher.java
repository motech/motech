package org.motechproject.mds.web.matcher;

import org.motechproject.mds.dto.TypeDto;
import org.springframework.context.MessageSource;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

/**
 * The <code>TypeMatcher</code> checks if the field type display name matches the given term.
 */
public class TypeMatcher extends MdsMatcher<TypeDto> {
    private MessageSource messageSource;

    public TypeMatcher(final String term, final MessageSource messageSource) {
        super(TypeDto.class, term);
        this.messageSource = messageSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean match(TypeDto obj) {
        String label = messageSource.getMessage(obj.getDisplayName(), null, null);
        boolean startsWith = startsWithIgnoreCase(label, getFirstTerm());

        return isBlank(getTerm()) || startsWith;
    }
}
