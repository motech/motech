package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.motechproject.mobileforms.api.valueobjects.GroupNameAndForms;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FormDownloadServlet extends BaseFormServlet {
    public static final byte ACTION_DOWNLOAD_STUDY_LIST = 2;
    public static final byte ACTION_DOWNLOAD_USERS_AND_FORMS = 11;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ZOutputStream zOutput = new ZOutputStream(response.getOutputStream(), JZlib.Z_BEST_COMPRESSION);
        DataInputStream dataInput = new DataInputStream(request.getInputStream());
        DataOutputStream dataOutput = new DataOutputStream(zOutput);
        try {
            readParameters(dataInput);
            byte action = dataInput.readByte();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            if (action == ACTION_DOWNLOAD_STUDY_LIST)
                handleDownloadStudies(byteStream);
            else if (action == ACTION_DOWNLOAD_USERS_AND_FORMS)
                handleDownloadUsersAndForms(byteStream, dataInput);

            dataOutput.writeByte(RESPONSE_SUCCESS);
            dataOutput.write(byteStream.toByteArray());
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

    private void handleDownloadStudies(ByteArrayOutputStream byteStream) throws Exception {
        EpihandyXformSerializer serializer = serializer();
        serializer.serializeStudies(byteStream, mobileFormsService.getAllFormGroups());
    }

    private void handleDownloadUsersAndForms(ByteArrayOutputStream byteStream, DataInputStream dataInput) throws Exception {
        EpihandyXformSerializer epiSerializer = serializer();
        epiSerializer.serializeUsers(byteStream, usersService.getUsers());
        int studyIndex = dataInput.readInt();
        GroupNameAndForms groupNameAndForms = mobileFormsService.getForms(studyIndex);
        epiSerializer.serializeForms(byteStream, groupNameAndForms.getForms(), studyIndex, groupNameAndForms.getGroupName());
    }
}