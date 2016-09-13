package org.motechproject.tasks.domain.enums;

import org.motechproject.tasks.domain.mds.task.Filter;
import org.motechproject.tasks.domain.mds.task.FilterSet;

/**
 * Enumerates logical operators that can be used on {@link Filter}s in the task {@link FilterSet}s.
 */
public enum LogicalOperator {
    AND, OR
}
