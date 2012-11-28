package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

public class FormDownloadServlet extends BaseFormServlet {
    private final Logger log = LoggerFactory.getLogger(FormUploadServlet.class);

    public static final byte ACTION_DOWNLOAD_STUDY_LIST = 2;
    public static final byte ACTION_DOWNLOAD_USERS_AND_FORMS = 11;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ZOutputStream zOutput = new ZOutputStream(response.getOutputStream(), JZlib.Z_BEST_COMPRESSION);
        DataInputStream dataInput = new DataInputStream(request.getInputStream());
        DataOutputStream dataOutput = new DataOutputStream(zOutput);
        try {
            readParameters(dataInput);
            byte action = readActionByte(dataInput);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            if (action == ACTION_DOWNLOAD_STUDY_LIST) {
                handleDownloadStudies(byteStream);

            } else if (action == ACTION_DOWNLOAD_USERS_AND_FORMS) {
                handleDownloadUsersAndForms(byteStream, dataInput);
            }

            dataOutput.writeByte(RESPONSE_SUCCESS);
            dataOutput.write(byteStream.toByteArray());
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("successfully downloaded the xforms");

        } catch (Exception e) {
            dataOutput.writeByte(RESPONSE_ERROR);
            throw new ServletException(FAILED_TO_SERIALIZE_DATA, e);
        } finally {
            dataOutput.flush();
            zOutput.finish();
            response.flushBuffer();
        }
    }

    private void handleDownloadStudies(ByteArrayOutputStream byteStream) throws SerializerException {
        EpihandyXformSerializer serializer = serializer();
        try {
            serializer.serializeStudies(byteStream, getMobileFormsService().getAllFormGroups());
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }

    private void handleDownloadUsersAndForms(ByteArrayOutputStream byteStream, DataInputStream dataInput)
            throws IOException, SerializerException {
        EpihandyXformSerializer epiSerializer = serializer();
        try {
            epiSerializer.serializeUsers(byteStream, getUsersService().getUsers());
        } catch (Exception e) {
            throw new SerializerException(e);
        }

        int studyIndex = dataInput.readInt();
        FormGroup groupNameAndForms = getMobileFormsService().getForms(studyIndex);
        List<String> formsXmlContent = collect(groupNameAndForms.getForms(), on(Form.class).content());
        try {
            epiSerializer.serializeForms(byteStream, formsXmlContent, studyIndex, groupNameAndForms.getName());
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }
}
