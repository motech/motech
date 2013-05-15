package org.motechproject.ivr.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.ivr.calllog.domain.CallRecord;
import org.motechproject.ivr.calllog.domain.CallLogSearchParameters;
import org.motechproject.ivr.calllog.service.CallRecordsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/calllog")
public class CallLogController {    // service methods for angular ui

    @Autowired
    private CallRecordsSearchService calllogSearchService;

    // TODO: should return application/json content
    @RequestMapping("/search")
    @ResponseBody
    public String search(@ModelAttribute CallLogSearchParameters params) throws IOException {
        List<CallRecord> result = calllogSearchService.search(params);
        return new ObjectMapper().writeValueAsString(result);
    }

    @RequestMapping("/count")
    @ResponseBody
    public String count(@ModelAttribute CallLogSearchParameters params) throws IOException {
        HashMap<String, Long> map = new HashMap<>();
        map.put("count", calllogSearchService.count(params));
        return new ObjectMapper().writeValueAsString(map);
    }

    @RequestMapping("/maxduration")
    @ResponseBody
    public String findMaxCallDuration() throws IOException {
        HashMap<String, Long> map = new HashMap<>();
        map.put("maxDuration", calllogSearchService.findMaxCallDuration());
        return new ObjectMapper().writeValueAsString(map);
    }

    @RequestMapping("/phone-numbers")
    @ResponseBody
    public List<String> allPhoneNumbers() throws IOException {
        return calllogSearchService.getAllPhoneNumbers();
    }
}
