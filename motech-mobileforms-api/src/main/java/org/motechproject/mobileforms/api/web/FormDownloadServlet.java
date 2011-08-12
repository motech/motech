package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.motechproject.mobileforms.api.dao.AllMobileForms;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FormDownloadServlet extends HttpServlet {

    public static final byte ACTION_DOWNLOAD_STUDY_LIST = 2;
    public static final byte RESPONSE_ERROR = 0;
    public static final byte RESPONSE_SUCCESS = 1;

    private static ApplicationContext context;

    synchronized static public ApplicationContext getContext() {
        if (context == null){
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
            EpihandyXformSerializer epiSerializer = new EpihandyXformSerializer();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            if (action == ACTION_DOWNLOAD_STUDY_LIST) {
                AllMobileForms allMobileForms = (AllMobileForms) getContext().getBean("allMobileForms");
                epiSerializer.serializeStudies(byteArrayOutputStream, prepareEPIFormList(allMobileForms.getAllFormGroups()));
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

    private List<Object[]> prepareEPIFormList(List<FormGroup> allFormGroups) {
        List<Object[]> result = new ArrayList<Object[]>();
        if (allFormGroups == null) {
            return Collections.emptyList();
        }
        for (int index = 0; index < allFormGroups.size(); index++) {
            result.add(new Object[]{index, allFormGroups.get(index).getName()});
        }
        return result;
    }
}