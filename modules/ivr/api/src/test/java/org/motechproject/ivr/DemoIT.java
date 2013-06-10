package org.motechproject.ivr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.service.CallRecordsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class DemoIT {

    @Autowired
    private CallRecordsServiceImpl callRecordsService;

    @Test
    public void shouldFoo(){
      assertTrue(true);
    }
}
