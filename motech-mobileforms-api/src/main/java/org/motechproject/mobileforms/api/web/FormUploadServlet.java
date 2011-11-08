package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import org.apache.commons.lang.StringUtils;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.fcitmuk.epihandy.ResponseHeader;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.model.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormUploadServlet extends BaseFormServlet {

    private final Logger log = LoggerFactory.getLogger(FormUploadServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ZOutputStream zOutput = new ZOutputStream(response.getOutputStream(), JZlib.Z_BEST_COMPRESSION);
        DataInputStream dataInput = new DataInputStream(request.getInputStream());
        DataOutputStream dataOutput = new DataOutputStream(zOutput);
        response.setContentType(APPLICATION_OCTET_STREAM);

        List<FormError> allErrors = new ArrayList<FormError>();
        int success = 0;
        int failures = 0;
        try {
            readParameters(dataInput);
            byte action = dataInput.readByte();
            List<FormBean> formBeans = extractBeans(dataInput);
            for (FormBean formBean : formBeans) {
                List<FormError> formErrors = getValidatorFor(formBean).validate(formBean);
                if (formErrors.isEmpty()) {
                    formPublisher.publish(formBean);
                    success++;
                } else {
                    allErrors.addAll(formErrors);
                    failures++;
                }
            }
            dataOutput.writeByte(ResponseHeader.STATUS_SUCCESS);
            dataOutput.writeInt(success);
            dataOutput.writeInt(failures);
            if (failures > 0)
                writeErrors(dataOutput, allErrors);

            response.setStatus(HttpServletResponse.SC_OK);
            log.info("Forms processed: success=" + success + "|failures=" + failures);

        } catch (Exception e) {
            dataOutput.writeByte(RESPONSE_ERROR);
            throw new ServletException(FAILED_TO_SERIALIZE_DATA, e);
        } finally {
            dataOutput.flush();
            zOutput.finish();
            response.flushBuffer();
        }
    }

    private List<FormBean> extractBeans(DataInputStream dataInput) throws Exception {
        EpihandyXformSerializer serializer = serializer();
        serializer.addDeserializationListener(formProcessor);
        serializer.deserializeStudiesWithEvents(dataInput, mobileFormsService.getFormIdMap());
        return formProcessor.formBeans();
    }

    private void writeErrors(DataOutputStream dataOutput, List<FormError> allErrors) {

    }

}
