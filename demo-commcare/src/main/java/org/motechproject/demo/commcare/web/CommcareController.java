package org.motechproject.demo.commcare.web;

import org.joda.time.DateTime;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
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

	private void testSchedule() {
		EnrollmentRequest enrollmentRequest = new EnrollmentRequest("RussellTest", "Health Worker Form Submission Tracking", null, null, null, null, null, null, null);

		scheduleTrackingService.enroll(enrollmentRequest);
		EnrollmentRecord enrollmentRecord = scheduleTrackingService.getEnrollment("RussellTest", "Health Worker Form Submission Tracking");

		enrollmentRecord.getStartOfDueWindow();
		Enrollment enrollment = enrollments.getActiveEnrollment("RussellTest", "Health Worker Form Submission Tracking");

		DateTime early = enrollment.getStartOfWindowForCurrentMilestone(WindowName.earliest);
		DateTime due = enrollment.getStartOfWindowForCurrentMilestone(WindowName.due);
		DateTime late = enrollment.getStartOfWindowForCurrentMilestone(WindowName.late);
		DateTime max = enrollment.getStartOfWindowForCurrentMilestone(WindowName.max);

		scheduleTrackingService.fulfillCurrentMilestone("RussellTest", "Health Worker Form Submission Tracking");
		enrollment = enrollments.getActiveEnrollment("RussellTest", "Health Worker Form Submission Tracking");
		enrollmentRecord = scheduleTrackingService.getEnrollment("RussellTest", "Health Worker Form Submission Tracking");

		early = enrollment.getStartOfWindowForCurrentMilestone(WindowName.earliest);
		due = enrollment.getStartOfWindowForCurrentMilestone(WindowName.due);
		late = enrollment.getStartOfWindowForCurrentMilestone(WindowName.late);
		max = enrollment.getStartOfWindowForCurrentMilestone(WindowName.max);

		Schedule schedule = schedules.getByName("Health Worker Form Submission Tracking");

		schedule.getMilestone("blah").getData().get("FormID");

		List<Milestone> milestones = schedule.getMilestones();

		for (Milestone milestone : milestones) {
			System.out.println(milestone.getName());
			List <MilestoneWindow> windows = milestone.getMilestoneWindows();
			for (MilestoneWindow window: windows) {
				System.out.println(window.getPeriod());
			}
		}


	}

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

	@RequestMapping("/commcare")
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		//			testSchedule();

		BufferedReader reader = request.getReader();
		boolean end = false;
		String forwardedRequest = "";
		while (!end) {
			String line = reader.readLine();
			if (line == null) { end = true; } else {
				forwardedRequest += line;
			}
		}
		System.out.println(forwardedRequest);

		CommcareParserUtil parser = new CommcareParserUtil(forwardedRequest);
		String xmlns = parser.findAttributeByElement("data", "xmlns");

		if (xmlns != null) {
			System.out.println("There's an xmlns");
			System.out.println(xmlns);
			System.out.println(isValidForm(xmlns));
			String commcareId = parser.getValueByElement("userID");
			if (commcareId != null) {
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

		Case caseInstance = parser.parseCaseFromForm(forwardedRequest);
		if (caseInstance != null) {
			System.out.println("There's a case");
		} else {
			System.out.println("No case");
		}

		return null;
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