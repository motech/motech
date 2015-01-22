package org.motechproject.mds.web.controller;

import org.motechproject.mds.docs.RestDocumentationGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller that serves json documentation of the REST API.
 * This output is then displayed by the Swagger UI.
 */
@Controller
public class RestDocumentationController {

    @Autowired
    private RestDocumentationGenerator docGenerator;

    @RequestMapping(value = "/rest-doc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void printMdsRestDocumentation() {

    }
}
