package org.motechproject.security.domain;

import java.util.Comparator;

import static org.motechproject.security.constants.SecurityConfigConstants.SYSTEM_ORIGIN;

/**
 * Class that helps to compare {@link org.motechproject.security.domain.MotechURLSecurityRule}
 */
public class SecurityRuleComparator implements Comparator<MotechURLSecurityRule> {

    /**
     * Compares two MotechURLSecurityRules to select more
     * important one (one with higher priority or one that comes
     * from the system or one with longer pattern).
     * First checks if both priorities are the same,
     * if yes then checks for origin of both rules.
     * If both of origins equals {@link org.motechproject.security.constants.SecurityConfigConstants#SYSTEM_ORIGIN}
     * or if none of them then compares length of patterns from both rules.
     * If origin of only one of the rules is equal then returns
     * number that represents given rule. If priorities
     * of both rules are not the same then just compares
     * them.
     *
     * @param o1 first rule
     * @param o2 second rule
     * @return number that represents one of the rules - will
     * return 1 if first rule is more important or -1 if the
     * second one
     */
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
