package org.motechproject.mobileforms.api.web;

import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.motechproject.mobileforms.api.callbacks.StudyProcessor;
import org.motechproject.mobileforms.api.service.MobileFormsService;
import org.motechproject.mobileforms.api.service.UsersService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.IOException;

public abstract class BaseFormServlet extends HttpServlet {
    public static final byte RESPONSE_ERROR = 0;
    public static final byte RESPONSE_SUCCESS = 1;

    public static final String FAILED_TO_SERIALIZE_DATA = "failed to serialize data";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    protected ApplicationContext context;
    protected UsersService usersService;
    protected MobileFormsService mobileFormsService;
    protected StudyProcessor studyProcessor;

    protected BaseFormServlet() {
        context =  new ClassPathXmlApplicationContext("applicationMobileFormsAPI.xml");
        mobileFormsService = context.getBean("mobileFormsServiceImpl", MobileFormsService.class);
        usersService = context.getBean("usersServiceImpl", UsersService.class);
        studyProcessor = context.getBean("studyProcessor", StudyProcessor.class);
    }

    protected EpihandyXformSerializer serializer() {
        return new EpihandyXformSerializer();
    }

    protected void readParameters(DataInputStream dataInput) throws IOException {
        String name = dataInput.readUTF();
        String password = dataInput.readUTF();
        String serializer = dataInput.readUTF();
        String locale = dataInput.readUTF();
    }

    @Override
    protected abstract void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
