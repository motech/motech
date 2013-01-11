package org.motechproject.ivr.calllog.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.decisiontree.core.CallDetail;
import org.motechproject.decisiontree.server.service.CalllogSearchParameters;
import org.motechproject.decisiontree.server.service.CalllogSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Controller
public class CallLogController {    // service methods for angular ui

    @Autowired
    private CalllogSearchService calllogSearchService;

    @RequestMapping("/search")
    @ResponseBody
    public String getAllObjects(@RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String minDuration,
                                @RequestParam(required = false) String maxDuration,
                                @RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate) throws IOException {
        CalllogSearchParameters params = new CalllogSearchParameters();
        params.setPhoneNumber(phoneNumber);
        params.setMinDuration(toInt(minDuration));
        params.setMaxDuration(toInt(maxDuration));
        params.setStartTime(toDate(fromDate));
        params.setEndTime(toDate(toDate));
        List<CallDetail> result = calllogSearchService.search(params);
        return new ObjectMapper().writeValueAsString(result);
    }

    private Integer toInt(String valString) {
        return isNotBlank(valString)?Integer.valueOf(valString):null;
    }

    private DateTime toDate(String date) {
        return isNotBlank(date)?DateTime.parse(date, DateTimeFormat.forPattern("mm/dd/yyyy HH:mm")):null;
    }
}
