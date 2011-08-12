package org.motechproject.scheduletracking.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationScheduleTrackingAPI.xml"})
public class ScheduleTrackingServiceTest {
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Test
    public void testAutowiring() {
        assertNotNull(scheduleTrackingService);
    }
}
