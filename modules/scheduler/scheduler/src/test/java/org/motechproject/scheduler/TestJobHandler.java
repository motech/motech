package org.motechproject.scheduler;


import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class TestJobHandler {

    List<DateTime> triggeredTime = new ArrayList<>();

    @MotechListener(subjects = "TestSubject")
    public void handleRepeatJob(MotechEvent event) {
        triggeredTime.add(DateUtil.now());
    }

    List<DateTime> getTriggeredTime(){
        return triggeredTime;
    }
}
