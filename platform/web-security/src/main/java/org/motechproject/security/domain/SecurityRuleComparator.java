package org.motechproject.security.domain;

import java.util.Comparator;

import static org.motechproject.security.constants.SecurityConfigConstants.SYSTEM_ORIGIN;

public class SecurityRuleComparator implements Comparator<MotechURLSecurityRule> {

    @Override
    public int compare(MotechURLSecurityRule o1, MotechURLSecurityRule o2) {
        Integer priority1 = o1.getPriority();
        Integer priority2 = o2.getPriority();

        if (priority1 == priority2) {
            if (SYSTEM_ORIGIN.equals(o1.getOrigin()) && SYSTEM_ORIGIN.equals(o2.getOrigin())) {
                return compareLength(o1.getPattern(), o2.getPattern());
            } else if (SYSTEM_ORIGIN.equals(o1.getOrigin())) {
                return 1;
            } else if (SYSTEM_ORIGIN.equals(o2.getOrigin())) {
                return -1;
            } else {
                return compareLength(o1.getPattern(), o2.getPattern());
            }
        }

        return (priority1 < priority2) ? 1 : -1;
    }

    private int compareLength(String pattern1, String pattern2) {
        if (pattern1.length() > pattern2.length()) {
            return -1;
        } else if (pattern1.length() < pattern2.length()) {
            return 1;
        }
        return pattern1.compareTo(pattern2);
    }
}
