package org.motechproject.email.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.service.EmailRecordSearchCriteria;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.email.service.impl.EmailAuditServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EmailControllerTest {
    @Mock
    private EmailSenderService senderService;

    @Mock
    private EmailAuditServiceImpl auditService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private EmailController emailController = new EmailController();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(auditService.findAllEmailRecords()).thenReturn(getTestEmailRecords());
        when(auditService.findEmailRecords(any(EmailRecordSearchCriteria.class))).thenReturn(getTestEmailRecords());
        setUpSecurityContextWithEmailAdminPermission();
    }

    @Test
    public void shouldReturnProperMailsForAutoComplete() {
        List<String> available = emailController.getAvailableMails("subject", "abc");
        List<String> available2 = emailController.getAvailableMails("subject", "def");
        List<String> available3 = emailController.getAvailableMails("subject", "abc@g");

        assertNotNull(available);
        assertNotNull(available2);
        assertNotNull(available3);
        assertThat(available.size(), is(2));
        assertThat(available2.size(), is(2));
        assertThat(available3.size(), is(1));
        assertThat(available3.get(0), is("abc@gmail.com"));
    }

    @Test
    public void shouldReturnEmailRecords() {
        GridSettings filter = new GridSettings();
        filter.setDeliveryStatus("SENT,ERROR");
        filter.setPage(1);
        filter.setRows(10);

        when(auditService.countEmailRecords(any(EmailRecordSearchCriteria.class))).thenReturn(54L);

        EmailRecords<? extends BasicEmailRecordDto> records = emailController.getEmails(filter, request);
        assertEquals(Integer.valueOf(1), records.getPage());
        assertEquals(Integer.valueOf(54), records.getRecords());
        assertEquals(Integer.valueOf(6), records.getTotal());

        ArgumentCaptor<EmailRecordSearchCriteria> captor = ArgumentCaptor.forClass(EmailRecordSearchCriteria.class);

        verify(auditService).findEmailRecords(captor.capture());
        assertEquals(filter.getPage(), captor.getValue().getQueryParams().getPage());
        assertEquals(filter.getRows(), captor.getValue().getQueryParams().getPageSize());
    }

    private void setUpSecurityContextWithEmailAdminPermission() {
        SecurityContext securityContext = new SecurityContextImpl();
        Authentication authentication = new UsernamePasswordAuthenticationToken("emailtestuser", "testpass", asList(new SimpleGrantedAuthority("viewDetailedEmailLogs")));
        securityContext.setAuthentication(authentication);
        authentication.setAuthenticated(false);
        SecurityContextHolder.setContext(securityContext);
    }

    private List<EmailRecord> getTestEmailRecords() {
        List<EmailRecord> records = new ArrayList<>();
        records.add(new EmailRecord("abc@gmail.com", "def@gmail.com", "subject", "message",
               new DateTime(1000), DeliveryStatus.SENT));
        records.add(new EmailRecord("def@gmail.com", "abc@gmail.com", "subject2", "message2",
               new DateTime(2000), DeliveryStatus.SENT));
        records.add(new EmailRecord("abc@yahoo.com", "def@gmail.com", "Asubject3", "message3",
               new DateTime(3000), DeliveryStatus.SENT));
        records.add(new EmailRecord("abc@yahoo.com", "abc@gmail.com", "subject4", "message4",
               new DateTime(5000), DeliveryStatus.SENT));
        records.add(new EmailRecord("abc@yahoo.com", "def@yahoo.com", "Bsubject5", "message5",
               new DateTime(4000), DeliveryStatus.SENT));
        return records;
    }

}
