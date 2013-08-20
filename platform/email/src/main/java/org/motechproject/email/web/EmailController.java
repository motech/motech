package org.motechproject.email.web;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.api.CsvConverter;
import org.motechproject.commons.api.Range;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.domain.EmailRecordComparator;
import org.motechproject.email.domain.EmailRecords;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailRecordSearchCriteria;
import org.motechproject.security.service.MotechRoleService;
import org.motechproject.security.service.MotechUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.CharEncoding.UTF_8;

/**
 * The <code>EmailController</code> class is used by view layer for getting information
 * about all {@Link EmailRecords} or single {@Link EmailRecord}. It stores the most recent
 * records and allows filtering and sorting them by given criteria.
 */

@Controller
public class EmailController {

    @Autowired
    private EmailAuditService auditService;

    @Autowired
    private MotechUserService motechUserService;

    @Autowired
    private MotechRoleService motechRoleService;

    private EmailRecords previousEmailRecords;

    @RequestMapping(value = "/emails", method= RequestMethod.GET)
    @ResponseBody
    public EmailRecords getEmails(GridSettings filter) {
        List<EmailRecord> filtered = auditService.findEmailRecords(prepareCriteria(filter));


        boolean sortAscending = filter.getSortDirection() == null ? true : filter.getSortDirection().equals("asc");

        if (filter.getSubject()!=null) {
            filtered = filterByPartialString(filtered, filter.getSubject());
        }

        if (filter.getSortColumn()!=null && (!filtered.isEmpty())) {
            Collections.sort(
                    filtered, new EmailRecordComparator(sortAscending, filter.getSortColumn())
            );
        }

        previousEmailRecords = hideColumns(filtered, filter);
        return previousEmailRecords;
    }

    @RequestMapping(value = "/emails/months/", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableMonths() {
        List<String> availableMonths = new ArrayList<>();

        List<EmailRecord> records = auditService.findAllEmailRecords();
        for (EmailRecord record : records) {
            String month = record.getDeliveryTime().monthOfYear().getAsText();
            String year = record.getDeliveryTime().year().getAsText();

            if (!availableMonths.contains(month+" "+year)) {
                availableMonths.add(month+" "+year);
            }
        }

        return availableMonths;
    }

    @RequestMapping(value = "/emails/export", method = RequestMethod.GET)
    public void exportEmailLog(@RequestParam("range") String range,
                               @RequestParam(value = "month", required = false) String month,
                               HttpServletResponse response) throws IOException {
        DateTime now = new DateTime();
        String fileName = "motech_email_logs_"+now.toString("yyyy-MM-dd_HH-kk-mm");
        response.setContentType("text/csv;charset=utf-8");
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename="+fileName+".csv");

        EmailRecords toSave = new EmailRecords();

        if (range.equals("all")) {
            GridSettings allEmailsFilter = new GridSettings();
            List<EmailRecord> allEmails = auditService.findAllEmailRecords();
            allEmailsFilter.setPage(1);
            allEmailsFilter.setRows(allEmails.size());
            toSave = hideColumns(allEmails, allEmailsFilter);
        } else if (range.equals("table")) {
            toSave = previousEmailRecords;
        } else if (range.equals("month") && (!month.isEmpty())) {
            int moved = 0;
            String fixedMonth = "0";
            if (month.charAt(0)=='0') {
                fixedMonth = month.substring(1);
                moved++;
            } else {
                fixedMonth = month;
            }
            GridSettings oneMonthFilter = new GridSettings();
            DateTime monthBegin = new DateTime(Integer.parseInt(fixedMonth.substring(3-moved,7-moved)),
                    Integer.parseInt(fixedMonth.substring(0,2-moved)),
                    1, 0, 0);
            DateTime monthFall = new DateTime().withYear(Integer.parseInt(fixedMonth.substring(3-moved,7-moved))).
                    withMonthOfYear(Integer.parseInt(fixedMonth.substring(0,2-moved))).
                    dayOfMonth().withMaximumValue().
                    hourOfDay().withMaximumValue().
                    minuteOfHour().withMaximumValue().
                    secondOfMinute().withMaximumValue().
                    millisOfSecond().withMaximumValue();
            List<EmailRecord> monthEmails = auditService.findEmailRecords(new EmailRecordSearchCriteria().
                    withMessageTimeRange(new Range(monthBegin, monthFall)));
            oneMonthFilter.setPage(1);
            oneMonthFilter.setRows(monthEmails.size());
            toSave = hideColumns(monthEmails, oneMonthFilter);
        }

        response.getWriter().write(CsvConverter.convertToCSV(prepareForCsvConversion(toSave)));
    }


    @RequestMapping(value = "/emails/available/", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableMails(@RequestParam("autoComplete") String autoComplete,
                                          @RequestParam("term") String partialAddress) {

        List<String> availableAddress = new ArrayList<>();

        if (autoComplete.equals("subject") && emailCredentials("viewDetailedEmailLogs")) {
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

    @RequestMapping(value = "/emails/{mailid}", method= RequestMethod.GET)
    @ResponseBody
    public EmailRecords getEmail(@PathVariable int mailid) {
        EmailRecords record = null;
        if (previousEmailRecords != null) {
            record = new EmailRecords(1, 1, asList(previousEmailRecords.getRows().get(mailid - 1)));
        }
        return record;
    }

    private EmailRecordSearchCriteria prepareCriteria(GridSettings filter) {
        EmailRecordSearchCriteria criteria = new EmailRecordSearchCriteria();
        criteria.withMessageTimeRange(new Range<>(( (filter.getTimeFrom() == null || filter.getTimeFrom().isEmpty()) ? getMinDateTime() :
                DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime(filter.getTimeFrom())),
                ( (filter.getTimeTo() == null || filter.getTimeTo().isEmpty()) ? getMaxDateTime() :
                        DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss").parseDateTime(filter.getTimeTo()))));

        if (filter.getDeliveryStatusFromSettings()==null ? false : (!filter.getDeliveryStatusFromSettings().isEmpty())) {
            criteria = criteria.withDeliveryStatuses(filter.getDeliveryStatusFromSettings());
        }

        return criteria;
    }

    private List<EmailRecord> filterByPartialString(List<EmailRecord> records, String partial) {
        List<EmailRecord> filtered = new ArrayList<>();

        for (EmailRecord record : records) {
            if (record.getFromAddress().contains(partial) || record.getToAddress().contains(partial)
                    || record.getSubject().contains(partial)) {
                filtered.add(record);
            }
        }

        return filtered;
    }

    private List<String> getAllFromAddressContaining(String partial) {
        List<String> available = new ArrayList<>();

        List <EmailRecord> records = auditService.findAllEmailRecords();
        for (EmailRecord record : records) {
            if (record.getFromAddress().contains(partial) && (!available.contains(record.getFromAddress()))) {
                available.add(record.getFromAddress());
            }
        }

        return available;
    }

    private List<String> getAllToAddressContaining(String partial) {
        List<String> available = new ArrayList<>();

        List <EmailRecord> records = auditService.findAllEmailRecords();
        for (EmailRecord record : records) {
            if (record.getToAddress().contains(partial) && (!available.contains(record.getToAddress()))) {
                available.add(record.getToAddress());
            }
        }

        return available;
    }

    private DateTime getMinDateTime() {
        return new DateTime(0);
    }

    private DateTime getMaxDateTime() {
        return new DateTime(Long.MAX_VALUE);
    }

    private boolean emailCredentials(String permissionType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = motechUserService.getRoles(auth == null ? "" : auth.getName());

        for (String role : roles) {
            List<String> permissions = motechRoleService.getRole(role).getPermissionNames();
            if (permissions.contains(permissionType)) {
                return true;
            }
        }

        return false;
    }

    private EmailRecords hideColumns(List<EmailRecord> records, GridSettings filter) {
        EmailRecords mailRecords;

        if (emailCredentials("viewDetailedEmailLogs")) {
            List<EmailRecordDto> recordsDto = new ArrayList<>();
            for (EmailRecord record : records) {
                recordsDto.add(new EmailRecordDto(record));
            }
            mailRecords = new EmailRecords<>(filter.getPage()==null ? 1 : filter.getPage(),
                    filter.getRows(), recordsDto);
        } else if (emailCredentials("viewBasicEmailLogs") && (filter.getSubject()==null ? true : filter.getSubject().isEmpty()) ) {
            List<BasicEmailRecordDto> basicList = new ArrayList<>();
            for (EmailRecord rec : records) {
                basicList.add(new BasicEmailRecordDto(rec));
            }
            mailRecords = new EmailRecords<>(filter.getPage()==null ? 1 : filter.getPage(),
                    filter.getRows(), basicList);
        } else {
            mailRecords = new EmailRecords<>(0,0,new ArrayList());
        }
        return mailRecords;
    }

    private List<List<String>> prepareForCsvConversion(EmailRecords records) {
        List<List<String>> list = new ArrayList<>();
        if (records.getRows().size() > 0 && records.getRows().get(0) instanceof EmailRecordDto) {
            list.add(asList("Status", "Delivery time", "From address", "To address", "Subject", "Message"));
        } else if (records.getRows().size() > 0 && records.getRows().get(0) instanceof  BasicEmailRecordDto) {
            list.add(asList("Status", "Delivery time"));
        }
        for(Object record : records.getRows()) {
            if (record instanceof EmailRecordDto) {
                List<String> innerList = asList(((EmailRecordDto) record).getDeliveryStatus(),
                        ((EmailRecordDto) record).getDeliveryTime(),
                        ((EmailRecordDto) record).getFromAddress(),
                        ((EmailRecordDto) record).getToAddress(),
                        ((EmailRecordDto) record).getSubject(),
                        ((EmailRecordDto) record).getMessage());
                list.add(innerList);
            } else if (record instanceof BasicEmailRecordDto) {
                List<String> innerList = asList(((BasicEmailRecordDto) record).getDeliveryStatus(),
                        ((BasicEmailRecordDto) record).getDeliveryTime());
                list.add(innerList);
            }
        }
        return list;
    }

}
