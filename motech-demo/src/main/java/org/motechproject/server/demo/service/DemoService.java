package org.motechproject.server.demo.service;

import java.util.Date;

public interface DemoService {

    void schedulePhoneCall(String phoneNumber, Date callTime);

    void initiatePhoneCall(String phoneNumber);

}
