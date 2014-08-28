package org.motechproject.mds.rest.web;

import org.motechproject.mds.rest.MdsRestFacade;
import org.motechproject.mds.util.ClassName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class MdsRestController  {

    private static final Logger LOG = LoggerFactory.getLogger(MdsRestController.class);

    private ApplicationContext applicationContext;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public void get() {
        LOG.info("GET");
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public void post() {
        LOG.info("POST");
    }

    @RequestMapping(value = "/put", method = RequestMethod.PUT)
    public void put() {
        LOG.info("PUT");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete() {
        LOG.info("DELETE");
    }

    private MdsRestFacade getRestFacade(String entityName, String module, String namespace) {
        String restId = ClassName.restId(entityName, module, namespace);
        return applicationContext.getBean(restId, MdsRestFacade.class);
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
