package org.motechproject.mds.web.controller;

import org.motechproject.mds.service.RestDocumentationService;
import org.motechproject.server.ui.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller that serves json documentation of the REST API.
 * This output is then displayed by the Swagger UI.
 */
@Controller
public class RestDocumentationController {

    @Autowired
    private RestDocumentationService restDocService;

    @Autowired
    private LocaleService localeService;

    /**
     * Prints the spec of the MDS REST API to the response. The server prefix is used for substituting the
     * base path in the schema.
     *
     * @param request  the request sent do the server
     * @param serverPrefix  the server prefix for this server(deduced using javascript), the most common one is /motech-platform-server
     * @param response  the response to which the documentation will be written to
     * @throws IOException if there were problems writing the documentation to the response
     */
    @RequestMapping(value = "/rest-doc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void printMdsRestDocumentation(HttpServletRequest request,
                                          @RequestParam(value = "serverPrefix", required = false) String serverPrefix,
                                              HttpServletResponse response) throws IOException {
        restDocService.retrieveDocumentation(response.getWriter(), serverPrefix, localeService.getUserLocale(request));
    }
}
