package org.motechproject.demo.commcare.web;

import org.joda.time.DateTime;
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

	private String scheduleName = "Health Worker Form Submission Tracking";

	@Autowired
	private CommcareUserService commcareUserService;

	@Autowired
	@Qualifier(value = "validForms")
	private Properties validForms;

	@Autowired
	private AllTrackedSchedules schedules;

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
	public ModelAndView endSchedule(HttpServletRequest request,
			HttpServletResponse response) {
		//TODO
		return null;
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
			System.out.println("There's an xmlns");
			System.out.println(xmlns);
			System.out.println(isValidForm(xmlns));
			String commcareId = parser.getValueByElement("userID");
			if (commcareId != null && isValidForm(xmlns)) {
				System.out.println("ID of: " + commcareId);
				Enrollment enrollment = enrollments.getActiveEnrollment(commcareId, scheduleName);
				if (enrollment != null) {
					handleFormForEnrollment(enrollment, commcareId, scheduleName);
				}
			} 
		} else {

			String registrationXmlns = parser.findAttributeByElement("n0:registration", "xmlns:n0");
			System.out.println("Registration form: " + registrationXmlns);
			if (registrationXmlns.equals("http://openrosa.org/user-registration")) {
				String enrollmentId = parser.getValueByElement("uuid");
				System.out.println(enrollmentId);
				if (enrollmentId != null) {
					System.out.println("Enrolling...");
					EnrollmentRequest enrollmentRequest = new EnrollmentRequest(enrollmentId, "Quick Health Worker Form Submission Tracking", null, null, null, null, null, null, null);
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
		
		if (caseInstance != null) {
			System.out.println("There's a case");
		} else {
			System.out.println("No case");
		}
		
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
			scheduleTrackingService.fulfillCurrentMilestone(commcareId, scheduleName);
		}
	}

	private boolean isValidForm(String formId) {
		return validForms.containsValue(formId);
	}






}