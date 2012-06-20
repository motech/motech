package org.motechproject.server.demo.web;

import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.demo.service.DemoEventHandler;
import org.motechproject.server.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Controller
public class CallMeController {

    @Autowired
    @Qualifier("demoService")
    private DemoService demoService;

    @Autowired
    @Qualifier("demoEventHandler")
    private DemoEventHandler demoEventHandler;

    private List<IVRService> ivrServices;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public void home(HttpServletRequest request, HttpServletResponse response) throws Exception {
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

    @RequestMapping(value = "/jsp", method = RequestMethod.GET)
    public ModelAndView jspPage() {
        ModelAndView mav = new ModelAndView("service");

        mav.addObject("services", ivrServices);
        mav.addObject("current", demoEventHandler.getIvrService());

        return mav;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void homeSubmitForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("service"));
        IVRService service = ivrServices.get(id);
        int dot = service.toString().lastIndexOf('.');
        int at = service.toString().lastIndexOf('@');

        demoEventHandler.setIvrService(service);

        response.getWriter().write("Selected IVR Service: " + service.toString().substring(dot + 1, at));
    }

    @RequestMapping(value = "/scheduleCall", method = RequestMethod.GET)
    public void scheduleCall(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String phoneNumber = request.getParameter("phone");
        int delay = Integer.parseInt(request.getParameter("callDelay"));

        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, delay);
        Date callTime = now.getTime();

        demoService.schedulePhoneCall(phoneNumber, callTime);

        response.getWriter().write("Scheduled a phone call to " + phoneNumber + " at " + callTime);
    }

    @RequestMapping(value = "/initiateCall", method = RequestMethod.GET)
    public void initiateCall(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        String phoneNumber = request.getParameter("phone");

        demoService.initiatePhoneCall(phoneNumber);

        response.getWriter().write("Initiated a phone call to " + phoneNumber);
    }

    public void setIvrServices(List<IVRService> ivrServices) {
        this.ivrServices = ivrServices;

        demoEventHandler.setIvrService(ivrServices.isEmpty() ? null : ivrServices.get(0));
    }
}
