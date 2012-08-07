package org.motechproject.cmslite.api.web;

import org.apache.commons.io.IOUtils;
import org.ektorp.AttachmentInputStream;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

@Controller
public class ResourceController {

    private static final String NOT_FOUND_RESPONSE = "Content not found";

    private static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private CMSLiteService cmsLiteService;

    @RequestMapping(value = "/stream/{language}/{name}", method = RequestMethod.GET)
    public void getStreamContent(@PathVariable String language, @PathVariable String name, HttpServletResponse response)
            throws IOException {
        LOG.info(String.format("Getting resource for : stream:%s:%s", language, name));

        OutputStream out = null;
        AttachmentInputStream contentStream = null;

        try {
            out = response.getOutputStream();

            contentStream = (AttachmentInputStream) cmsLiteService.getStreamContent(language, name).getInputStream();

            response.setContentLength((int) contentStream.getContentLength());
            response.setContentType(contentStream.getContentType());
            response.setHeader("Accept-Ranges", "bytes");
            response.setStatus(HttpServletResponse.SC_OK);

            IOUtils.copy(contentStream, out);
        } catch (ContentNotFoundException e) {
            LOG.error(String.format("Content not found for : stream:%s:%s\n:%s", language, name,
                    Arrays.toString(e.getStackTrace())));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, NOT_FOUND_RESPONSE);
        } finally {
            IOUtils.closeQuietly(contentStream);
            IOUtils.closeQuietly(out);
        }
    }

    @RequestMapping(value = "/string/{language}/{name}", method = RequestMethod.GET)
    public void getStringContent(@PathVariable String language, @PathVariable String name, HttpServletResponse response)
            throws IOException {
        LOG.info(String.format("Getting resource for : string:%s:%s", language, name));

        PrintWriter writer = null;

        try {
            writer = response.getWriter();

            StringContent stringContent = cmsLiteService.getStringContent(language, name);

            response.setContentLength(stringContent.getValue().length());
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_OK);

            writer.print(stringContent.getValue());
        } catch (ContentNotFoundException e) {
            LOG.error(String.format("Content not found for : string:%s:%s\n:%s", language, name,
                    Arrays.toString(e.getStackTrace())));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, NOT_FOUND_RESPONSE);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}