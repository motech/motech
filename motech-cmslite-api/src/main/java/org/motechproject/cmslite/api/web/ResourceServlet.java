package org.motechproject.cmslite.api.web;

import org.apache.log4j.Logger;
import org.ektorp.AttachmentInputStream;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * REST service for CMS Lite content retrieval. Implementer should define and map this servlet in web.xml
 */
public class ResourceServlet extends HttpServlet {
    private static ApplicationContext context;
    private Logger logger = Logger.getLogger(this.getClass());
    private static final int BUFFER_SIZE = 1024 * 4;

    public static synchronized ApplicationContext getContext() {
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
            logger.info("Getting resource for : " + resourceQuery.getType() + ":" + resourceQuery.getLanguage() + ":" + resourceQuery.getName());
            if ("stream".equalsIgnoreCase(resourceQuery.getType())) {
                addStreamContentToResponse(response, cmsLiteService, resourceQuery);
            } else {
                addStringContentToResponse(response, cmsLiteService, resourceQuery);
            }
        } catch (ContentNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("Content not found for : " + resourceQuery.getType() + ":" + resourceQuery.getLanguage() + ":" + resourceQuery.getName() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    private void addStringContentToResponse(HttpServletResponse response, CMSLiteService cmsLiteService, ResourceQuery resourceQuery) throws ContentNotFoundException, IOException {
        PrintWriter writer = response.getWriter();
        try {
            StringContent stringContent = cmsLiteService.getStringContent(resourceQuery.getLanguage(), resourceQuery.getName());
            long contentLength = stringContent.getValue().length();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Content-Type", "text/plain");
            response.setContentLength((int) contentLength);

            writer.print(stringContent.getValue());
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    private void addStreamContentToResponse(HttpServletResponse response, CMSLiteService cmsLiteService, ResourceQuery resourceQuery) throws ContentNotFoundException, IOException {
        AttachmentInputStream contentStream = null;
        OutputStream fo = null;
        try {
            contentStream = (AttachmentInputStream) cmsLiteService.getStreamContent(resourceQuery.getLanguage(), resourceQuery.getName()).getInputStream();
            long contentLength = contentStream.getContentLength();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Content-Type", contentStream.getContentType());
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentLength((int) contentLength);

            fo = response.getOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = contentStream.read(buffer)) >= 0) {
                if (read > 0) {
                    fo.write(buffer, 0, read);
                }
            }
        } finally {
            if (contentStream != null) { contentStream.close(); }
            if (fo != null) {
                fo.flush();
                fo.close();
            }
        }
    }

    private ResourceQuery resourceQuery(HttpServletRequest request) {
        String requestURL = request.getRequestURI();
        String contextPathOnly = request.getContextPath();
        String servletPathOnly = request.getServletPath();
        String[] resourcePaths = requestURL.replace(contextPathOnly, "").replace(servletPathOnly, "").substring(1).split("/");
        String type = resourcePaths[0];
        String language = resourcePaths[1];
        String name = resourcePaths[2];
        return new ResourceQuery(language, name, type);
    }
}
