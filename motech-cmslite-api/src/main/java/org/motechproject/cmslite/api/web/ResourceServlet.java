package org.motechproject.cmslite.api.web;

import org.apache.log4j.Logger;
import org.ektorp.AttachmentInputStream;
import org.motechproject.cmslite.api.CMSLiteService;
import org.motechproject.cmslite.api.ResourceNotFoundException;
import org.motechproject.cmslite.api.ResourceQuery;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.util.Arrays;

public class ResourceServlet extends HttpServlet {

    private static ApplicationContext context;
    private Logger logger = Logger.getLogger(this.getClass());

    synchronized static public ApplicationContext getContext() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext("applicationCmsLiteApi.xml");
        }
        return context;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CMSLiteService cmsLiteService = (CMSLiteService) getContext().getBean("cmsLiteService");
        ResourceQuery resourceQuery = resourceQuery(request);

        try {

            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Content-Type", "audio/x-wav");

            logger.info("Getting resource for : " + resourceQuery.getLanguage() + ":" + resourceQuery.getName());
            AttachmentInputStream audioStream = (AttachmentInputStream) cmsLiteService.getContent(resourceQuery);

            AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 8000, 16, 1, 2, 8000, false);
            AudioInputStream audioInputStream1 = new AudioInputStream(audioStream, audioFormat, audioStream.getContentLength());
            AudioSystem.write(audioInputStream1, AudioFileFormat.Type.WAVE, response.getOutputStream());

        } catch (ResourceNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("Resource not found for : " + resourceQuery.getLanguage() + ":" + resourceQuery.getName() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    private ResourceQuery resourceQuery(HttpServletRequest request) {
        String requestURL = request.getRequestURI();
        String contextPathOnly = request.getContextPath();
        String servletPathOnly = request.getServletPath();
        String[] resourcePaths = requestURL.replace(contextPathOnly, "").replace(servletPathOnly, "").substring(1).split("/");
        String language = resourcePaths[0];
        String name = resourcePaths[1];
        return new ResourceQuery(name, language);
    }
}