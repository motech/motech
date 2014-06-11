package org.motechproject.mds.web.comparator;

import org.motechproject.mds.dto.TypeDto;
import org.springframework.context.MessageSource;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The <code>TypeDisplayNameComparator</code> compares two objects of
 * {@link org.motechproject.mds.dto.TypeDto} type by value of their display name key
 * (it ignores case differences in values).
 */
public class TypeDisplayNameComparator implements Comparator<TypeDto>, Serializable {
    private static final long serialVersionUID = 9000500317266112167L;

    private MessageSource messageSource;

    public TypeDisplayNameComparator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(TypeDto one, TypeDto two) {
        String oneLabel = messageSource.getMessage(one.getDisplayName(), null, null);
        String twoLabel = messageSource.getMessage(two.getDisplayName(), null, null);

        return oneLabel.compareToIgnoreCase(twoLabel);
    }
}
