package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.fcitmuk.epihandy.ResponseHeader;
import org.motechproject.mobileforms.api.vo.Study;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public class FormUploadServlet extends BaseFormServlet {

    private final Logger log = LoggerFactory.getLogger(FormUploadServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ZOutputStream zOutput = new ZOutputStream(response.getOutputStream(), JZlib.Z_BEST_COMPRESSION);
        DataInputStream dataInput = new DataInputStream(request.getInputStream());
        DataOutputStream dataOutput = new DataOutputStream(zOutput);
        response.setContentType(APPLICATION_OCTET_STREAM);

        try {
            readParameters(dataInput);
            byte action = dataInput.readByte();

            EpihandyXformSerializer serializer = serializer();
            serializer.addDeserializationListener(studyProcessor);
            serializer.deserializeStudiesWithEvents(dataInput, mobileFormsService.getFormIdMap());

            Integer faultyForms = 0;
            Integer processedForms = studyProcessor.formsCount();
            log.info("successfully uploaded " + processedForms + " forms");

            dataOutput.writeByte(ResponseHeader.STATUS_SUCCESS);
            dataOutput.writeInt(processedForms);
            dataOutput.writeInt(faultyForms);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            dataOutput.writeByte(RESPONSE_ERROR);
            throw new ServletException(FAILED_TO_SERIALIZE_DATA, e);
        } finally {
            dataOutput.flush();
            zOutput.finish();
            response.flushBuffer();
        }

    }
}
