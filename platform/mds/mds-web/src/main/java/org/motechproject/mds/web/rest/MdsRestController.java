package org.motechproject.mds.web.rest;

import org.motechproject.mds.ex.rest.RestNotSupportedException;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.rest.MdsRestFacade;
import org.motechproject.mds.util.ClassName;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rest")
public class MdsRestController  {

    private static final Logger LOG = LoggerFactory.getLogger(MdsRestController.class);

    @Autowired
    private BundleContext bundleContext;

    @RequestMapping(value = "/{moduleName}/{namespace}/{entityName}", method = RequestMethod.GET)
    @ResponseBody
    public List get(@PathVariable String moduleName, @PathVariable String namespace,
                    @PathVariable String entityName, @RequestParam Map<String, String> requestParams) {
        return doGet(entityName, moduleName, namespace, requestParams);
    }

    @RequestMapping(value = "/{moduleName}/{entityName}", method = RequestMethod.GET)
    @ResponseBody
    public List get(@PathVariable String moduleName, @PathVariable String entityName,
                    @RequestParam Map<String, String> requestParams) {
        return doGet(entityName, moduleName, null, requestParams);
    }

    @RequestMapping(value = "/{entityName}", method = RequestMethod.GET)
    @ResponseBody
    public List get(@PathVariable String entityName, @RequestParam Map<String, String> requestParams) {
        return doGet(entityName, null, null, requestParams);
    }

    private List doGet(String entityName, String moduleName, String namespace,
                       Map<String, String> requestParams) {
        MdsRestFacade restFacade = getRestFacade(entityName, moduleName, namespace);
        QueryParams queryParams = ParamParser.buildQueryParams(requestParams);
        return restFacade.get(queryParams);
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

        MdsRestFacade restFacade = null;
        try {
            String filter = String.format("(%s=%s)", "org.eclipse.gemini.blueprint.bean.name", restId);
            Collection<ServiceReference<MdsRestFacade>> refs = bundleContext.getServiceReferences(
                    MdsRestFacade.class, filter);

            if (refs != null && refs.size() > 1 && LOG.isWarnEnabled()) {
                LOG.warn("More then one Rest Facade matching for entityName={}, module={}, namespace={}. " +
                        "Using first one available.",
                        new Object[] { entityName, module, namespace });
            }

            if (refs != null && refs.size() > 0) {
                ServiceReference<MdsRestFacade> ref = refs.iterator().next();
                restFacade = bundleContext.getService(ref);
            }
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid Syntax for Rest Facade retrieval", e);
        }

        if (restFacade == null) {
            throw new RestNotSupportedException(entityName, module, namespace);
        }

        return restFacade;
    }
}
