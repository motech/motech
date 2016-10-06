package org.motechproject.email.web;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.api.Range;
import org.motechproject.email.builder.EmailRecordSearchCriteria;
import org.motechproject.email.constants.EmailRolesConstants;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.motechproject.commons.api.Records;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang.CharEncoding.UTF_8;

/**
 * The <code>EmailController</code> class is used by view layer for getting information
 * about all {@link Records} or single {@link EmailRecord}. It stores the most recent
 * records and allows filtering and sorting them by given criteria.
 */

@Controller
public class EmailController {

    private Map<String, GridSettings> lastFilter = new HashMap<>();

    @Autowired
    private EmailAuditService auditService;

    @RequestMapping(value = "/emails", method = RequestMethod.GET)
    @PreAuthorize(EmailRolesConstants.HAS_ANY_EMAIL_ROLE)
    @ResponseBody
    public Records<? extends BasicEmailRecordDto> getEmails(GridSettings filter, HttpServletRequest request) {
        EmailRecordSearchCriteria criteria = prepareCriteria(filter);

        List<EmailRecord> filtered = auditService.findEmailRecords(criteria);

        List<? extends BasicEmailRecordDto> rows = hideColumns(filtered, filter);

        long total = auditService.countEmailRecords(criteria);
        if (filter.getRows() == null) {
            int defaultRowsAmount = 10;
            filter.setRows(defaultRowsAmount);
        }
        if (filter.getPage() == null) {
            int defaultPage = 1;
            filter.setPage(defaultPage);
        }
        int totalPages = (int) Math.ceil((double) total / filter.getRows());

        String username = getUsername(request);
        if (username != null) {
            lastFilter.put(username, filter);
        }

        return new Records<>(filter.getPage(), totalPages, (int) total, rows);
    }

    @RequestMapping(value = "/emails/{mailid}", method = RequestMethod.GET)
    @PreAuthorize(EmailRolesConstants.HAS_ANY_EMAIL_ROLE)
    @ResponseBody
    public Records<EmailRecordDto> getEmail(@PathVariable int mailid) {
        EmailRecordDto record = new EmailRecordDto(auditService.findById(mailid));
        return new Records<>(1, 1, 1, Arrays.asList(record));
    }

    @RequestMapping(value = "/emails/months/", method = RequestMethod.GET)
    @PreAuthorize(EmailRolesConstants.HAS_ANY_EMAIL_ROLE)
    @ResponseBody
    public List<String> getAvailableMonths() {
        List<String> availableMonths = new ArrayList<>();

        List<EmailRecord> records = auditService.findAllEmailRecords();
        for (EmailRecord record : records) {
            String month = record.getDeliveryTime().monthOfYear().getAsText();
            String year = record.getDeliveryTime().year().getAsText();

            if (!availableMonths.contains(month + " " + year)) {
                availableMonths.add(month + " " + year);
            }
        }

        return availableMonths;
    }

    @RequestMapping(value = "/emails/export", method = RequestMethod.GET)
    @PreAuthorize(EmailRolesConstants.HAS_ANY_EMAIL_ROLE)
    public void exportEmailLog(@RequestParam("range") String range,
                               @RequestParam(value = "month", required = false) String month,
                               HttpServletResponse response,
                               HttpServletRequest request) throws IOException {

        DateTime now = new DateTime();

        List<? extends BasicEmailRecordDto> toSave = new ArrayList<>();

        if ("all".equals(range)) {
            GridSettings allEmailsFilter = new GridSettings();
            List<EmailRecord> allEmails = auditService.findAllEmailRecords();
            allEmailsFilter.setPage(1);
            allEmailsFilter.setRows(allEmails.size());
            toSave = hideColumns(allEmails, allEmailsFilter);
        } else if ("table".equals(range)) {
            GridSettings filter = lastFilter.get(getUsername(request));
            toSave = getEmails(filter, request).getRows();
        } else if ("month".equals(range) && (!month.isEmpty())) {
            int moved = 0;
            String fixedMonth;
            if (month.charAt(0) == '0') {
                fixedMonth = month.substring(1);
                moved++;
            } else {
                fixedMonth = month;
            }
            GridSettings oneMonthFilter = new GridSettings();
            DateTime monthBegin = new DateTime(Integer.parseInt(fixedMonth.substring(3 - moved, 7 - moved)), // NO CHECKSTYLE MagicNumber
                    Integer.parseInt(fixedMonth.substring(0, 2 - moved)), 1, 0, 0);
            DateTime monthFall = new DateTime().withYear(Integer.parseInt(fixedMonth.substring(3 - moved, 7 - moved))).  // NO CHECKSTYLE MagicNumber
                    withMonthOfYear(Integer.parseInt(fixedMonth.substring(0, 2 - moved))).
                    dayOfMonth().withMaximumValue().
                    hourOfDay().withMaximumValue().
                    minuteOfHour().withMaximumValue().
                    secondOfMinute().withMaximumValue().
                    millisOfSecond().withMaximumValue();
            Set<DeliveryStatus> allDeliveryStatuses = Sets.newHashSet(DeliveryStatus.values());

            List<EmailRecord> monthEmails = auditService.findEmailRecords(new EmailRecordSearchCriteria().
                    withMessageTimeRange(new Range<>(monthBegin, monthFall)).withDeliveryStatuses(allDeliveryStatuses));
            oneMonthFilter.setPage(1);
            oneMonthFilter.setRows(monthEmails.size());

            toSave = hideColumns(monthEmails, oneMonthFilter);
        }

        String fileName = "motech_email_logs_" + now.toString("yyyy-MM-dd_HH-kk-mm");
        response.setContentType("text/csv;charset=utf-8");
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + fileName + ".csv");

        try (CsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {

            String[] headers;
            String[] fieldMapping;
            if (toSave.size() == 0 || toSave.get(0) instanceof EmailRecordDto) {
                headers = new String[]{"Delivery Status", "Delivery Time", "From Address", "To Address", "Subject", "Message"};
                fieldMapping = new String[]{"deliveryStatus", "deliveryTime", "fromAddress", "toAddress", "subject", "message"};
            } else {
                headers = new String[]{"Delivery Status", "Delivery Time"};
                fieldMapping = new String[]{"deliveryStatus", "deliveryTime"};
            }

            csvBeanWriter.writeHeader(headers);

            for (BasicEmailRecordDto email : toSave) {
                csvBeanWriter.write(email, fieldMapping);
            }

        }
    }

    @RequestMapping(value = "/emails/available/", method = RequestMethod.GET)
    @PreAuthorize(EmailRolesConstants.HAS_ANY_EMAIL_ROLE)
    @ResponseBody
    public List<String> getAvailableMails(@RequestParam("autoComplete") String autoComplete,
                                          @RequestParam("term") String partialAddress) {

        List<String> availableAddress = new ArrayList<>();

        if ("subject".equals(autoComplete) && emailCredentials(EmailRolesConstants.DETAILED_EMAIL_LOGS)) {
            availableAddress = getAllFromAddressContaining(partialAddress);
            List<String> availableAddress2 = getAllToAddressContaining(partialAddress);

            for (String address : availableAddress2) {
                if (!availableAddress.contains(address)) {
                    availableAddress.add(address);
                }
            }
        }

        return availableAddress;
    }

    private EmailRecordSearchCriteria prepareCriteria(GridSettings filter) {
        EmailRecordSearchCriteria criteria = new EmailRecordSearchCriteria();

        DateTime from = StringUtils.isBlank(filter.getTimeFrom()) ? null :
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime(filter.getTimeFrom());
        DateTime to = StringUtils.isBlank(filter.getTimeTo()) ? null :
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime(filter.getTimeTo());

        criteria.withMessageTimeRange(new Range<>(from, to));

        if (filter.getDeliveryStatusFromSettings() != null && (!filter.getDeliveryStatusFromSettings().isEmpty())) {
            criteria = criteria.withDeliveryStatuses(filter.getDeliveryStatusFromSettings());
        }

        Order sortOrder = new Order(filter.getSortColumn(), filter.getSortDirection());
        QueryParams queryParams = new QueryParams(filter.getPage(), filter.getRows(), sortOrder);
        criteria.withQueryParams(queryParams);

        criteria.withSubject(filter.getSubject());
        criteria.withToAddress(filter.getSubject());
        criteria.withFromAddress(filter.getSubject());
        criteria.withMessage(filter.getSubject());

        return criteria;
    }

    private List<String> getAllFromAddressContaining(String partial) {
        List<String> available = new ArrayList<>();

        List<EmailRecord> records = auditService.findAllEmailRecords();
        for (EmailRecord record : records) {
            if (record.getFromAddress().contains(partial) && (!available.contains(record.getFromAddress()))) {
                available.add(record.getFromAddress());
            }
        }

        return available;
    }

    private List<String> getAllToAddressContaining(String partial) {
        List<String> available = new ArrayList<>();

        List<EmailRecord> records = auditService.findAllEmailRecords();
        for (EmailRecord record : records) {
            if (record.getToAddress().contains(partial) && (!available.contains(record.getToAddress()))) {
                available.add(record.getToAddress());
            }
        }

        return available;
    }

    private boolean emailCredentials(String permissionType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().contains(new SimpleGrantedAuthority(permissionType));
    }

    private List<? extends BasicEmailRecordDto> hideColumns(List<EmailRecord> records, GridSettings filter) {
        if (emailCredentials(EmailRolesConstants.DETAILED_EMAIL_LOGS)) {
            List<EmailRecordDto> mailRecords = new ArrayList<>();
            for (EmailRecord record : records) {
                mailRecords.add(new EmailRecordDto(record));
            }
            return mailRecords;
        } else if (emailCredentials(EmailRolesConstants.BASIC_EMAIL_LOGS) && (filter.getSubject() == null || filter.getSubject().isEmpty())) {
            List<BasicEmailRecordDto> mailRecords = new ArrayList<>();
            for (EmailRecord rec : records) {
                mailRecords.add(new BasicEmailRecordDto(rec));
            }
            return mailRecords;
        } else {
            return new ArrayList<>();
        }
    }

    private String getUsername(HttpServletRequest request) {
        String username = null;

        if (request != null) {
            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                username = principal.getName();
            }
        }

        return username;
    }
}
