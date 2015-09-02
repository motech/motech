package org.motechproject.email.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.email.service.impl.EmailAuditServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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

    @Mock
    private HttpServletResponse response;

    @Mock
    Map<String, GridSettings> lastFilter;

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

    @Test
    public void shouldExportEmailAllAsCsv() throws Exception {
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        emailController.exportEmailLog("all", null, response, request);

        assertEquals(getTestEmailRecordsAsCsv(), writer.toString());
    }

    @Test
    public void shouldExportEmailTableAsCsv() throws Exception {
        StringWriter writer = new StringWriter();

        GridSettings filter = new GridSettings();
        filter.setDeliveryStatus("SENT");
        filter.setPage(1);
        filter.setRows(5);
        filter.setTimeFrom("1969-01-01 00:00:00");
        filter.setTimeTo("1969-01-31 23:59:59");

        when(lastFilter.get(anyString())).thenReturn(filter);
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        emailController.exportEmailLog("table", null, response, request);

        ArgumentCaptor<EmailRecordSearchCriteria> captor = ArgumentCaptor.forClass(EmailRecordSearchCriteria.class);

        verify(auditService).findEmailRecords(captor.capture());

        assertArrayEquals(new DeliveryStatus[]{DeliveryStatus.SENT}, captor.getValue().getDeliveryStatuses().toArray());
        assertEquals(filter.getPage(), captor.getValue().getQueryParams().getPage());
        assertEquals(filter.getRows(), captor.getValue().getQueryParams().getPageSize());
        assertTrue(captor.getValue().getDeliveryTimeRange().getMin().isEqual(LocalDateTime.parse("1969-01-01T00:00:00")));
        assertTrue(captor.getValue().getDeliveryTimeRange().getMax().isEqual(LocalDateTime.parse("1969-01-31T23:59:59")));
        assertEquals(getTestEmailRecordsAsCsv(), writer.toString());
    }

    @Test
    public void shouldExportEmailMonthAsCsv() throws Exception {
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));
        emailController.exportEmailLog("month", "01-1969", response, request);

        ArgumentCaptor<EmailRecordSearchCriteria> captor = ArgumentCaptor.forClass(EmailRecordSearchCriteria.class);

        verify(auditService).findEmailRecords(captor.capture());

        assertTrue(captor.getValue().getDeliveryTimeRange().getMin().isEqual(LocalDateTime.parse("1969-01-01T00:00:00.000000000")));
        assertTrue(captor.getValue().getDeliveryTimeRange().getMax().isEqual(LocalDateTime.parse("1969-01-31T23:59:59.999999999")));
        assertEquals(writer.toString(), getTestEmailRecordsAsCsv());
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
               LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC), DeliveryStatus.SENT));
        records.add(new EmailRecord("def@gmail.com", "abc@gmail.com", "subject2", "message2",
               LocalDateTime.ofEpochSecond(2, 0, ZoneOffset.UTC), DeliveryStatus.SENT));
        records.add(new EmailRecord("abc@yahoo.com", "def@gmail.com", "Asubject3", "message3",
               LocalDateTime.ofEpochSecond(3, 0, ZoneOffset.UTC), DeliveryStatus.SENT));
        records.add(new EmailRecord("abc@yahoo.com", "abc@gmail.com", "subject4", "message4",
               LocalDateTime.ofEpochSecond(5, 0, ZoneOffset.UTC), DeliveryStatus.SENT));
        records.add(new EmailRecord("abc@yahoo.com", "def@yahoo.com", "Bsubject5", "message5",
               LocalDateTime.ofEpochSecond(4, 0, ZoneOffset.UTC), DeliveryStatus.SENT));
        return records;
    }

    private String getTestEmailRecordsAsCsv() {
        List<EmailRecordDto> emailRecordDtos = new ArrayList<>();
        for (EmailRecord email : getTestEmailRecords()) {
            emailRecordDtos.add(new EmailRecordDto(email));
        }

        StringBuilder sb = new StringBuilder();

        sb.append("Delivery Status,Delivery Time,From Address,To Address,Subject,Message\r\n");

        for (EmailRecordDto dto : emailRecordDtos) {
            sb.append(String.format("%s,%s,%s,%s,%s,%s\r\n",
                dto.getDeliveryStatus(),
                dto.getDeliveryTime(),
                dto.getFromAddress(),
                dto.getToAddress(),
                dto.getSubject(),
                dto.getMessage()
            ));
        }

        return sb.toString();
    }
}
