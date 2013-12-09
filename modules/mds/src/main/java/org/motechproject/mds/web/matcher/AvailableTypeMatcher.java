package org.motechproject.mds.web.matcher;

import org.motechproject.mds.dto.AvailableTypeDto;
import org.springframework.context.MessageSource;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

/**
 * The <code>AvailableTypeMatcher</code> checks if the field type display name matches the given
 * term.
 */
public class AvailableTypeMatcher extends MdsMatcher<AvailableTypeDto> {
    private MessageSource messageSource;

    public AvailableTypeMatcher(final String term, final MessageSource messageSource) {
        super(AvailableTypeDto.class, term);
        this.messageSource = messageSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean match(AvailableTypeDto obj) {
        String label = messageSource.getMessage(obj.getType().getDisplayName(), null, null);
        boolean startsWith = startsWithIgnoreCase(label, getFirstTerm());

        return isBlank(getTerm()) || startsWith;
    }
}
