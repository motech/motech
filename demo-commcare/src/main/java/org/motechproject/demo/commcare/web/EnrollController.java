package org.motechproject.demo.commcare.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EnrollController {
	
	@Autowired
	private AllTrackedSchedules schedules;
	
	@Autowired
	private AllEnrollments enrollments;
	
    @Autowired
	private ScheduleTrackingService scheduleTrackingService;

	@RequestMapping("/add")
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response)  {
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

		
		return null;
	}
	
	@RequestMapping("/remove")
	public ModelAndView remove(HttpServletRequest request,
			HttpServletResponse response)  {
		return null;
	}
	}

