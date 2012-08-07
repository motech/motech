package org.motechproject.scheduler;


import org.joda.time.DateTime;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.util.DateUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
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
