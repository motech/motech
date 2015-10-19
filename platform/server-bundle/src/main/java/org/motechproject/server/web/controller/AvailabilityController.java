package org.motechproject.server.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class responsible for determining available UI tabs for modules where
 * UI tabs availability depends on different user permissions.
 */
@Controller
public class AvailabilityController {

    public static final String MANAGE_IVR = "manageIVR";
    public static final String VIEW_IVR_LOGS_PERMISSION = "viewIVRLogs";
    public static final String MANAGE_SMS_PERMISSION = "manageSMS";
    public static final String VIEW_SMS_LOGS_PERMISSION = "viewSMSLogs";
    private static final String MANAGE_MTRAINING = "manageMTraining";
    private static final String VIEW_MTRAINING_LOGS = "viewMTrainingLogs";

    @RequestMapping(value = "/available/{moduleName}", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableTabs(@PathVariable String moduleName) {

        if (moduleName.equals("ivr")) {
            return tabsForIVR();
        } else if (moduleName.equals("sms")) {
            return tabsForSMS();
        } else if (moduleName.equals("mTraining")) {
            return tabsForMTraining();
        } else {
            return null;
        }
    }

    private List<String> tabsForIVR() {

        List<String> availableTabs = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(MANAGE_IVR))) {
            availableTabs.add("templates");
            availableTabs.add("settings");
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(VIEW_IVR_LOGS_PERMISSION))) {
            availableTabs.add("log");
        }

        return availableTabs;
    }

    private List<String> tabsForSMS() {

        List<String> availableTabs = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(MANAGE_SMS_PERMISSION))) {
            availableTabs.add("send");
            availableTabs.add("settings");
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(VIEW_SMS_LOGS_PERMISSION))) {
            if (availableTabs.isEmpty()) {
                availableTabs.add("log");
            } else {
                availableTabs.add(1, "log");
            }
        }

        return availableTabs;
    }

    private List<String> tabsForMTraining() {

        List<String> availableTabs = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(MANAGE_MTRAINING))) {
            availableTabs.add("treeView");
            availableTabs.add("courses");
            availableTabs.add("chapters");
            availableTabs.add("quizzes");
            availableTabs.add("lessons");
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(VIEW_MTRAINING_LOGS))) {
            availableTabs.add("activityRecords");
            availableTabs.add("bookmarks");
        }

        return availableTabs;
    }
}
