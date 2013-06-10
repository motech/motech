package org.motechproject.server.demo.web;

import org.motechproject.ivr.service.contract.IVRService;
import org.motechproject.server.demo.service.DemoEventHandler;
import org.motechproject.server.demo.service.DemoService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Controller
public class CallMeController implements InitializingBean {

    public static final String HAS_ROLE_CALLIVR = "hasRole('callIVR')";
    @Autowired
    @Qualifier("demoService")
    private DemoService demoService;

    @Autowired
    @Qualifier("demoEventHandler")
    private DemoEventHandler demoEventHandler;

    @Resource(name = "ivrServiceList")
    private List<IVRService> ivrServices;

    private static final String CURRENT = "current";

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public void home(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();

        if (ivrServices.isEmpty()) {
            writer.write("Any IVR Service not found");
        } else {
            writer.write("<form method=\"post\">\n");
            writer.write("<select name=\"service\">\n");

            Iterator<IVRService> it = ivrServices.iterator();
            int index = 0;
            String format = "<option value=\"%d\" %s>%s</option>\n";

            while (it.hasNext()) {
                IVRService service = it.next();
                int dot = service.toString().lastIndexOf('.');
                int at = service.toString().lastIndexOf('@');
                String name = service.toString().substring(dot + 1, at);
                boolean selected = demoEventHandler.getIvrService() == null ? false : demoEventHandler.getIvrService() == service;

                writer.write(String.format(format, index, selected ? "selected=\"selected\"" : "", name));
                ++index;
            }

            writer.write("</select>\n");
            writer.write("<input type=\"submit\" value=\"Send\" />\n");
            writer.write("</form>\n");
        }
    }

    @RequestMapping(value = "/homeJsp", method = RequestMethod.GET)
    public ModelAndView jspPage() {
        ModelAndView mav = new ModelAndView("service");

        List<String> ivrServiceNames = new ArrayList<>();

        for (IVRService service : ivrServices) {
            String serviceName = getServiceName(service);
            ivrServiceNames.add(serviceName);
        }
        mav.addObject("services", ivrServiceNames);
        mav.addObject(CURRENT, getServiceName(demoEventHandler.getIvrService()));

        return mav;
    }

    @RequestMapping(value = "/homeJsp", method = RequestMethod.POST)
    public void homeJspSubmitForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("service"));
        IVRService service = ivrServices.get(id);

        demoEventHandler.setIvrService(service);

        response.getWriter().write("Selected IVR Service: " + getServiceName(service));
    }

   @RequestMapping(value = "/ivrcalls", method = RequestMethod.GET)
   @PreAuthorize(HAS_ROLE_CALLIVR)
    public ModelAndView ivrcalls() {
        ModelAndView mav = new ModelAndView("ivrcalls");

        List<String> ivrServiceNames = new ArrayList<>();

        for (IVRService service : ivrServices) {
            String serviceName = getServiceName(service);
            ivrServiceNames.add(serviceName);
        }
        mav.addObject("services", ivrServiceNames);
        if (demoEventHandler.getIvrService() != null) {
            mav.addObject(CURRENT, getServiceName(demoEventHandler.getIvrService()));
        } else {
            mav.addObject(CURRENT, null);
        }
        mav.addObject("message", "false");

        return mav;
    }

    @RequestMapping(value = "/ivrcalls", method = RequestMethod.POST)
    @PreAuthorize(HAS_ROLE_CALLIVR)
    public ModelAndView ivrcalls(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        ModelAndView mav = new ModelAndView("ivrcalls");
        int delay;
        String phoneNumber = request.getParameter("phoneNumber");
        int id = Integer.parseInt(request.getParameter("service"));
        IVRService servicePost = ivrServices.get(id);
        List<String> ivrServiceNames = new ArrayList<>();

        for (IVRService service : ivrServices) {
            String serviceName = getServiceName(service);
            ivrServiceNames.add(serviceName);
        }
        mav.addObject("services", ivrServiceNames);
        mav.addObject(CURRENT, getServiceName(demoEventHandler.getIvrService()));
        mav.addObject("message", "true");
        mav.addObject("callUrl", request.getParameter("callUrl"));

        if (request.getParameter("delay").isEmpty()) {
            delay = 0;
        } else {
            delay = Integer.parseInt(request.getParameter("delay"));
        }

        if (phoneNumber.isEmpty()) {
            mav.addObject("phoneNumberCall", "undefined");
        } else {
            mav.addObject("phoneNumberCall", phoneNumber);
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, delay);
            Date callTime = now.getTime();
            demoService.schedulePhoneCall(phoneNumber, callTime);
        }

        demoEventHandler.setIvrService(servicePost);

        return mav;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/scheduleCall", method = RequestMethod.GET)
    public void scheduleCall(@RequestParam String phone, @RequestParam String callDelay) {
        int delay = Integer.parseInt(callDelay);

        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, delay);
        Date callTime = now.getTime();

        demoService.schedulePhoneCall(phone, callTime);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/initiateCall", method = RequestMethod.GET)
    public void initiateCall(@RequestParam String phone) {
        demoService.initiatePhoneCall(phone);
    }

    public void setIvrServices(List<IVRService> ivrServices) {
        this.ivrServices.addAll(ivrServices);
    }

    @RequestMapping(value = "/ivrservices", method = RequestMethod.GET)
    @ResponseBody
    public List<ServiceContainer> getServices() {
        List<ServiceContainer> serviceList = new ArrayList<ServiceContainer>();

        int index = 0;
        for (IVRService service : ivrServices) {
            ServiceContainer serviceContainer = new ServiceContainer();

            int dot = service.toString().lastIndexOf('.');
            int at = service.toString().lastIndexOf('@');
            String name = service.toString().substring(dot + 1, at);

            serviceContainer.setName(name);
            serviceContainer.setIndex(index);

            serviceList.add(serviceContainer);
            index++;
        }
        return serviceList;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/changeservice", method = RequestMethod.GET)
    public void changeService(@RequestParam String id) {
        int index = Integer.parseInt(id);
        IVRService service = ivrServices.get(index);

        demoEventHandler.setIvrService(service);
    }

    public void afterPropertiesSet() {
        if (!ivrServices.isEmpty()) {
            demoEventHandler.setIvrService(ivrServices.get(0));
        }
    }

    private String getServiceName(IVRService service) {
        String name = service.toString();
        int dot = name.lastIndexOf('.');
        int at = name.lastIndexOf('@');
        return name.substring(dot + 1, at);
    }
}

