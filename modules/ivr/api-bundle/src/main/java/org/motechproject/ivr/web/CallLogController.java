package org.motechproject.ivr.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallRecordSearchParameters;
import org.motechproject.ivr.service.contract.CallRecordsSearchService;
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
    private CallRecordsSearchService calllogSearchServiceI;

    // TODO: should return application/json content
    @RequestMapping("/search")
    @ResponseBody
    public String search(@ModelAttribute CallRecordSearchParameters params) throws IOException {
        List<CallDetailRecord> result = calllogSearchServiceI.search(params);
        return new ObjectMapper().writeValueAsString(result);
    }

    @RequestMapping("/count")
    @ResponseBody
    public String count(@ModelAttribute CallRecordSearchParameters params) throws IOException {
        HashMap<String, Long> map = new HashMap<>();
        map.put("count", calllogSearchServiceI.count(params));
        return new ObjectMapper().writeValueAsString(map);
    }

    @RequestMapping("/maxduration")
    @ResponseBody
    public String findMaxCallDuration() throws IOException {
        HashMap<String, Long> map = new HashMap<>();
        map.put("maxDuration", calllogSearchServiceI.findMaxCallDuration());
        return new ObjectMapper().writeValueAsString(map);
    }

    @RequestMapping("/phone-numbers")
    @ResponseBody
    public List<String> allPhoneNumbers() throws IOException {
        return calllogSearchServiceI.getAllPhoneNumbers();
    }
}
