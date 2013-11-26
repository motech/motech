package org.motechproject.mds.web.comparator;

import org.motechproject.mds.dto.AvailableTypeDto;
import org.springframework.context.MessageSource;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The <code>AvailableTypeDisplayNameComparator</code> compares two objects of
 * {@link AvailableTypeDto} type by value of their display name key (it ignores case differences in
 * values).
 */
public class AvailableTypeDisplayNameComparator
        implements Comparator<AvailableTypeDto>, Serializable {
    private static final long serialVersionUID = 9000500317266112167L;

    private transient MessageSource messageSource;

    public AvailableTypeDisplayNameComparator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /** {@inheritDoc} */
    @Override
    public int compare(AvailableTypeDto one, AvailableTypeDto two) {
        String oneLabel = messageSource.getMessage(one.getType().getDisplayName(), null, null);
        String twoLabel = messageSource.getMessage(two.getType().getDisplayName(), null, null);

        return oneLabel.compareToIgnoreCase(twoLabel);
    }
}
