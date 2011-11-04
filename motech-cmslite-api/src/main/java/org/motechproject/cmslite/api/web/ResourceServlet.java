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
import java.io.IOException;
import java.io.OutputStream;
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
        AttachmentInputStream contentStream = null;
        try {
            logger.info("Getting resource for : " + resourceQuery.getLanguage() + ":" + resourceQuery.getName());
            contentStream = (AttachmentInputStream) cmsLiteService.getContent(resourceQuery);
            long contentLength = contentStream.getContentLength();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Content-Type", contentStream.getContentType());
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentLength((int) contentLength);
            
            OutputStream fo = response.getOutputStream();
    		byte [] buffer = new byte [1024*4];
    		int read ;
    		while((read=contentStream.read(buffer))>=0){
    			if (read>0)
    				fo.write(buffer,0,read);
    		}
        } catch (ResourceNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error("Resource not found for : " + resourceQuery.getLanguage() + ":" + resourceQuery.getName() + "\n" + Arrays.toString(e.getStackTrace()));
        } finally {
            if(contentStream != null) contentStream.close();
            request.getInputStream().close();
            response.getOutputStream().flush();
            response.getOutputStream().close();
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