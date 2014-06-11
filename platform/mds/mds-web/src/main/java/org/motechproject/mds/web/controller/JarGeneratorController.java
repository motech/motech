package org.motechproject.mds.web.controller;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.apache.commons.io.IOUtils;
import org.motechproject.mds.service.JarGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static org.apache.commons.lang.CharEncoding.UTF_8;

@Controller
public class JarGeneratorController extends MdsController {
    private static final String APPLICATION_JAVA_ARCHIVE = "application/java-archive";

    private JarGeneratorService jarGeneratorService;

    @RequestMapping(value = "/jar", method = RequestMethod.GET)
    public void generateJar(HttpServletResponse response) throws IOException, NotFoundException, CannotCompileException {
        response.setContentType(APPLICATION_JAVA_ARCHIVE);
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                format("attachment; filename=%s.jar", encode("mds-entities", UTF_8))
        );

        OutputStream output = response.getOutputStream();
        File jar = jarGeneratorService.generate();

        try (FileInputStream input = new FileInputStream(jar)) {
            IOUtils.copy(input, output);
        }
    }

    @Autowired
    public void setJarGeneratorService(JarGeneratorService jarGeneratorService) {
        this.jarGeneratorService = jarGeneratorService;
    }
}
