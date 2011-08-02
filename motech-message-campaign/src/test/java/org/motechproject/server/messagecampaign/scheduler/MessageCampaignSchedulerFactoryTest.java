package org.motechproject.server.messagecampaign.scheduler;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.EnrollRequest;
import org.motechproject.server.messagecampaign.domain.MessageCampaignException;
import org.motechproject.server.messagecampaign.domain.campaign.*;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageCampaignSchedulerFactoryTest {

    private MessageCampaignSchedulerFactory schedulerFactory;

    @Before
    public void setUp() {
        MotechSchedulerService motechSchedulerService = mock(MotechSchedulerService.class);
        initMocks(this);
        this.schedulerFactory = new MessageCampaignSchedulerFactory(motechSchedulerService);
    }

    @Test
    public void absoluteSchedulerTest() {
        AbsoluteCampaign campaign = new AbsoluteCampaign();
        EnrollRequest enrollmentRequest = new EnrollRequest();
        Assert.assertEquals(AbsoluteProgramScheduler.class, schedulerFactory.scheduler(enrollmentRequest, campaign).getClass());
    }

    @Test
    public void cronSchedulerTest() {
        CronBasedCampaign campaign = new CronBasedCampaign();
        EnrollRequest enrollmentRequest = new EnrollRequest();
        Assert.assertEquals(CronBasedProgramScheduler.class, schedulerFactory.scheduler(enrollmentRequest, campaign).getClass());
    }

    @Test
    public void offsetSchedulerTest() {
        OffsetCampaign campaign = new OffsetCampaign();
        EnrollRequest enrollmentRequest = new EnrollRequest();
        Assert.assertEquals(OffsetProgramScheduler.class, schedulerFactory.scheduler(enrollmentRequest, campaign).getClass());
    }

    @Test
    public void repeatingSchedulerTest() {
        RepeatingCampaign campaign = new RepeatingCampaign();
        EnrollRequest enrollmentRequest = new EnrollRequest();
        Assert.assertEquals(RepeatingProgramScheduler.class, schedulerFactory.scheduler(enrollmentRequest, campaign).getClass());
    }

    @Test(expected = MessageCampaignException.class)
    public void shouldThrowExceptionWhenCampaignTypeNotFound() {
        Campaign campaign = new Campaign<CampaignMessage>() {
            @Override
            public void messages(List<CampaignMessage> messages) {
            }

            @Override
            public List<CampaignMessage> messages() {
                return null;
            }
        };

        EnrollRequest enrollmentRequest = new EnrollRequest();
        schedulerFactory.scheduler(enrollmentRequest, campaign);
    }
}
