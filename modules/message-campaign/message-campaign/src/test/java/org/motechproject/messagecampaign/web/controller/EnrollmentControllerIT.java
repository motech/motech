package org.motechproject.messagecampaign.web.controller;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.messagecampaign.builder.CampaignMessageRecordTestBuilder;
import org.motechproject.messagecampaign.builder.CampaignRecordBuilder;
import org.motechproject.messagecampaign.contract.CampaignRequest;
import org.motechproject.messagecampaign.service.MessageCampaignService;
import org.motechproject.messagecampaign.userspecified.CampaignMessageRecord;
import org.motechproject.messagecampaign.userspecified.CampaignRecord;
import org.motechproject.messagecampaign.web.model.EnrollmentDto;
import org.motechproject.messagecampaign.web.model.EnrollmentList;
import org.motechproject.security.model.RoleDto;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class EnrollmentControllerIT {

    private static final String FRIDAY_CAMPAIGN = "Friday Campaign";
    private static final String USER_ID = "userId";
    @Autowired
    private EnrollmentController enrollmentController;

    @Autowired
    private MotechRoleService motechRoleService;

    @Autowired
    private MotechUserService motechUserService;

    @Autowired
    MessageCampaignService messageCampaignService;

    String userName = "testuser";
    String credentials = "testpass";

    @Before
    public void setUp() throws Exception {
        setUpSecurityContext();
    }

    @Test
    public void shouldGetAllEnrollments() throws Exception {
        createCampaignAndEnroll();
        final EnrollmentList allEnrollments = enrollmentController.getAllEnrollments(USER_ID, FRIDAY_CAMPAIGN);
        assertTrue(allEnrollments.getEnrollments().size() == 1);
        final EnrollmentDto enrollmentDto = allEnrollments.getEnrollments().get(0);
        assertEquals(USER_ID, enrollmentDto.getExternalId());
        assertEquals(FRIDAY_CAMPAIGN, enrollmentDto.getCampaignName());
    }

    private void createCampaignAndEnroll() {
        CampaignMessageRecord absoluteCampaignMessageRecord = CampaignMessageRecordTestBuilder.createAbsoluteCampaignMessageRecord(FRIDAY_CAMPAIGN, "msg1");
        absoluteCampaignMessageRecord.setName(FRIDAY_CAMPAIGN);
        final CampaignRecord campaignRecord = CampaignRecordBuilder.absoluteCampaignRecord(FRIDAY_CAMPAIGN, absoluteCampaignMessageRecord);
        messageCampaignService.saveCampaign(campaignRecord);
        final CampaignRequest enrollRequest =  new CampaignRequest(USER_ID, FRIDAY_CAMPAIGN, new LocalDate(2020, 7, 10), null, null); // Friday;
        messageCampaignService.startFor(enrollRequest);
    }

    @After
    public void tearDown() throws Exception {
        final UserDto user = new UserDto();
        user.setUserName(userName);
        motechUserService.deleteUser(user);
        motechRoleService.deleteRole(new RoleDto("testRole", null));
    }

    private void setUpSecurityContext() {
        RoleDto roles = new RoleDto("testRole", asList("addUser", "editUser", "deleteUser", "manageUser", "activateUser", "manageRole", "manageEnrollments", "manageCampaigns"));
        SecurityContext securityContext = new SecurityContextImpl();
        motechRoleService.createRole(roles);
        motechUserService.register(userName, credentials, "test@example.com", "testid", Arrays.asList("testRole"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userName, credentials);
        securityContext.setAuthentication(authentication);
        authentication.setAuthenticated(false);
        SecurityContextHolder.setContext(securityContext);
    }
}
