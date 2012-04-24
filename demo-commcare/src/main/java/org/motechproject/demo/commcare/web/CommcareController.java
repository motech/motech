package org.motechproject.demo.commcare.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.motechproject.commcare.domain.Case;
import org.motechproject.commcare.parser.CommcareCaseParser;
import org.motechproject.demo.commcare.domain.CommcareUser;
import org.motechproject.demo.commcare.services.CommcareUserService;
import org.motechproject.demo.commcare.util.CommcareParserUtil;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.MilestoneWindow;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Controller
public class CommcareController  {

	public static final String SCHEDULE_NAME = "Quick Health Worker Form Submission Tracking";

	@Autowired
	private CommcareUserService commcareUserService;

	@Autowired
	@Qualifier(value = "validForms")
	private Properties validForms;

	@Autowired
	private AllEnrollments enrollments;

	@Autowired
	private ScheduleTrackingService scheduleTrackingService;

	private Logger logger = LoggerFactory.getLogger((this.getClass()));


	@RequestMapping("/testCommcare")
	public ModelAndView testCommcare(HttpServletRequest request,
			HttpServletResponse response)  {

		List<CommcareUser> userList = commcareUserService.getAllUsers();

		for (CommcareUser user : userList) {
			System.out.println(user.getUsername());
		}
		return null;
	}

	@RequestMapping("/endSchedule") 
	public String endSchedule(HttpServletRequest request,
			HttpServletResponse response) {
		
		String userId = request.getParameter("user");
		
		if (userId != null) {
			List<String> scheduleNames = new ArrayList<String>();
			scheduleNames.add(SCHEDULE_NAME);
			scheduleTrackingService.unenroll(userId, scheduleNames);
		}
		
		return "Successfully removed user: " + userId;
	}
	

	@RequestMapping("/commcareforms")
	public ModelAndView handleForwardedForm(HttpServletRequest request,
			HttpServletResponse response) {
		
		String formXml = "";
		
		try {
			formXml = getRequestBodyAsString(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		CommcareParserUtil parser = null;
		try {
			parser = new CommcareParserUtil(formXml);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		String xmlns = parser.findAttributeByElement("data", "xmlns");
		
		if (xmlns != null) {
			String commcareId = parser.getValueByElement("userID");
			if (commcareId != null && isValidForm(xmlns)) {
				Enrollment enrollment = enrollments.getActiveEnrollment(commcareId, SCHEDULE_NAME);
				if (enrollment != null) {
					handleFormForEnrollment(enrollment, commcareId, SCHEDULE_NAME);
				}
			} 
		} else {
			String registrationXmlns = parser.findAttributeByElement("n0:registration", "xmlns:n0");
			if (registrationXmlns.equals("http://openrosa.org/user-registration")) {
				String enrollmentId = parser.getValueByElement("uuid");
				if (enrollmentId != null) {
					DateTime now = DateTime.now();
					LocalDate localDate = now.toLocalDate();
					Time time = new Time();
					time.setHour(now.getHourOfDay());
					time.setMinute(now.getMinuteOfHour());
					
					EnrollmentRequest enrollmentRequest = new EnrollmentRequest(enrollmentId, SCHEDULE_NAME, null, localDate, time, localDate, time, null, null);
					scheduleTrackingService.enroll(enrollmentRequest);
				}
			}
		}
		
		return null;
	}

	@RequestMapping("/commcarecases")
	public ModelAndView handleForwardedCase(HttpServletRequest request,
			HttpServletResponse response) {

		String caseXml = "";
		
		try {
			caseXml = getRequestBodyAsString(request);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		CommcareParserUtil parser = null;
		
		try {
			parser = new CommcareParserUtil(caseXml);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Case caseInstance = parser.parseCaseFromForm(caseXml);
		
		return null;
	}

	private String getRequestBodyAsString(HttpServletRequest request) throws IOException {
		BufferedReader reader = request.getReader();
		boolean end = false;
		String forwardedRequest = "";
		while (!end) {
			String line = reader.readLine();
			if (line == null) { end = true; } else {
				forwardedRequest += line;
			}
		}
		return forwardedRequest;
	}

	private void handleFormForEnrollment(Enrollment enrollment, String commcareId, String scheduleName) {
		DateTime now = DateTime.now();
		DateTime dueWindowStart = enrollment.getStartOfWindowForCurrentMilestone(WindowName.due);

		if (now.isAfter(dueWindowStart)) {
			LocalDate today = DateUtil.today();
			Time time = DateUtil.time(DateUtil.now());
			scheduleTrackingService.fulfillCurrentMilestone(commcareId, scheduleName, today, time);
		}
	}

	private boolean isValidForm(String formId) {
		return validForms.containsValue(formId);
	}






}