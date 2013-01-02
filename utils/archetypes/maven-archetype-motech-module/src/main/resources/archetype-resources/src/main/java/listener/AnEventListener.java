package ${groupId}.listener;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AnEventListener {

    @Autowired
    private SettingsFacade settings;

    @Autowired
    private MotechSchedulerService schedulerService;

    @MotechListener(subjects = "some_event")
    public void handle(MotechEvent event) {

        // read event payload
        Map<String, Object> eventParams = event.getParameters();
        String phoneNumber = (String) eventParams.get("phone_number");
        String message = (String) eventParams.get("message_for_user");

        // read your module's configuration
        Long repeatInterval = Long.valueOf(settings.getProperty("repeat_interval_in_millis"));
        Integer retries = Integer.valueOf(settings.getProperty("number_of_retries"));

        // do something, maybe schedule a job to send an sms at a particular time
        HashMap<String, Object> jobPayload = new HashMap<>();
        jobPayload.put("message", message);
        jobPayload.put("number", phoneNumber);

        schedulerService.scheduleRepeatingJob(new RepeatingSchedulableJob()
            .setStartTime(new Date())
            .setRepeatIntervalInMilliSeconds(repeatInterval)
            .setRepeatCount(retries)
            .setMotechEvent(new MotechEvent("sendSMS", jobPayload))
        );
    }
}
