package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.motechproject.mobileforms.api.service.MobileFormsService;
import org.motechproject.mobileforms.api.valueobjects.GroupNameAndForms;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FormDownloadServlet extends HttpServlet {

    public static final byte ACTION_DOWNLOAD_STUDY_LIST = 2;
    public static final byte ACTION_DOWNLOAD_USERS_AND_FORMS = 11;
    public static final byte RESPONSE_ERROR = 0;
    public static final byte RESPONSE_SUCCESS = 1;

    private static ApplicationContext context;

    synchronized static public ApplicationContext getContext() {
        if (context == null) {
            context = new ClassPathXmlApplicationContext("applicationMobileFormsAPI.xml");
        }
        return context;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InputStream input = request.getInputStream();
        OutputStream output = response.getOutputStream();

        ZOutputStream zOutput = new ZOutputStream(output, JZlib.Z_BEST_COMPRESSION);

        DataInputStream dataInput = new DataInputStream(input);
        DataOutputStream dataOutput = new DataOutputStream(zOutput);

        try {
            String name = dataInput.readUTF();
            String password = dataInput.readUTF();
            String serializer = dataInput.readUTF();
            String locale = dataInput.readUTF();

            byte action = dataInput.readByte();
            EpihandyXformSerializer epiSerializer = serializer();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            MobileFormsService mobileFormsService = getContext().getBean("mobileFormsService", MobileFormsService.class);
            if (action == ACTION_DOWNLOAD_STUDY_LIST) {
                epiSerializer.serializeStudies(byteArrayOutputStream, mobileFormsService.getAllFormGroups());
            } else if (action == ACTION_DOWNLOAD_USERS_AND_FORMS) {
                int studyIndex = dataInput.readInt();
                GroupNameAndForms groupNameAndForms = mobileFormsService.getForms(studyIndex);
                epiSerializer.serializeForms(byteArrayOutputStream, groupNameAndForms.getForms(), studyIndex, groupNameAndForms.getGroupName());
            }
            dataOutput.writeByte(RESPONSE_SUCCESS);
            dataOutput.write(byteArrayOutputStream.toByteArray());
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            dataOutput.writeByte(RESPONSE_ERROR);
            throw new ServletException("failed to serialize data", e);
        } finally {
            dataOutput.flush();
            zOutput.finish();
            response.flushBuffer();
        }
    }

    protected EpihandyXformSerializer serializer() {
        return new EpihandyXformSerializer();
    }


}