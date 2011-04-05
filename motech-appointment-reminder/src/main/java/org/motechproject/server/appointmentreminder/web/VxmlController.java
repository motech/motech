package org.motechproject.server.appointmentreminder.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public class VxmlController extends MultiActionController {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

	/**
	 * URL to request appointment reminder VoiceXML:
	 * http://localhost:8080/motech-platform-server/module/ar/vxml/ar
	 */
	public ModelAndView ar(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("In vxml controller");

		ModelAndView mav = new ModelAndView();
		mav.setViewName("ar");
		mav.addObject("message", "Appointment Reminder");
		return mav;
	}
}
