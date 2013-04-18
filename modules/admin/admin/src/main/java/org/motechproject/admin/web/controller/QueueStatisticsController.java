package org.motechproject.admin.web.controller;

import org.motechproject.admin.domain.Tenant;
import org.motechproject.admin.domain.QueueMBean;
import org.motechproject.admin.jmx.MBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Controller
public class QueueStatisticsController {

    @Autowired
    private MBeanService mBeanService;

    @Autowired
    @Qualifier("currentTenant")
    private Tenant tenant;


    @RequestMapping(value = "/queues", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<QueueMBean> browse() {
        return mBeanService.getQueueStatistics(tenant.getId());
    }


    @RequestMapping(value = "/queues/browse", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<QueueMessage> getMessages(@RequestParam(required = true) String queueName) {
        if (tenant.canHaveQueue(queueName)) {
            return mBeanService.getMessages(queueName);
        }
        return Collections.emptyList();
    }

}
