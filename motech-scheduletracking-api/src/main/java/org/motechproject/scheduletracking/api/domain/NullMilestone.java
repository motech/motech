package org.motechproject.scheduletracking.api.domain;

import org.motechproject.valueobjects.WallTime;

public class NullMilestone extends Milestone {
    public NullMilestone() {
        super("NULL Milestone", null, new WallTime(), new WallTime(), new WallTime(), new WallTime());
    }
}
