package org.motechproject.hub.web;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.motechproject.hub.exception.ApplicationErrors;
import org.motechproject.hub.exception.HubError;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.exception.RestException;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.HubTopicService;
import org.motechproject.hub.model.Modes;
import org.motechproject.hub.service.ContentDistributionService;
import org.motechproject.hub.service.SubscriptionService;
import org.motechproject.hub.util.HubConstants;
import org.motechproject.hub.validation.HubValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/hub")
public class HubController implements HubConstants {
	
	private final static Logger LOGGER = Logger.getLogger(HubController.class);
	
	@Autowired
	private HubValidator hubValidator;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private ContentDistributionService contentDistributionService;
	
	private HubTopicService hubTopicService;
	
	public HubValidator getHubValidator() {
		return hubValidator;
	}

	public void setHubValidator(HubValidator hubValidator) {
		this.hubValidator = hubValidator;
	}
	
	public SubscriptionService getSubscriptionService() {
		return subscriptionService;
	}

	public void setSubscriptionService(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	public ContentDistributionService getContentDistributionService() {
		return contentDistributionService;
	}

	public void setContentDistributionService(
			ContentDistributionService contentDistributionService) {
		this.contentDistributionService = contentDistributionService;
	}

	 @RequestMapping("/sayHello")
	 @ResponseBody
	 public String sayHello() {
		 /*HubTopic hubTopic = new HubTopic("topic_url_1");
		 hubTopicService.create(hubTopic);
		 hubTopic.setTopicUrl("topic_url_2");
		 hubTopicService.create(hubTopic);
		 List<HubTopic> hubTopics = hubTopicService.retrieveAll();
		 
	      return String.format("{\"message\":\"%s\"}", "Hello World " + hubTopics.size());*/
	      return String.format("{\"message\":\"%s\"}", "Hello World");
	 }
	 
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	@RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded", params = {HUB_CALLBACK_PARAM, HUB_MODE_PARAM, HUB_TOPIC_PARAM} )
	@ResponseBody public void subscribe (
			@RequestParam(value = HUB_CALLBACK_PARAM) String callbackUrl,
			@RequestParam(value = HUB_MODE_PARAM) String mode,
			@RequestParam(value = HUB_TOPIC_PARAM) String topic,
			@RequestParam(value = HUB_LEASE_SECONDS_PARAM, required = false) String leaseSeconds,
			@RequestParam(value = HUB_SECRET_PARAM, required = false) String secret)
			throws HubException {
		
		LOGGER.info("Request to " + mode + " started for topic " + topic + " from subscriber's callback url " + callbackUrl);
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			// TODO: decode url values?
			List<String> errors = hubValidator.validateSubscription(callbackUrl, mode, topic, leaseSeconds, secret);
			if (!errors.isEmpty()) {
				throw new HubException(ApplicationErrors.BAD_REQUEST, errors.toString());
			}
		Modes hubMode = Modes.valueOf(mode.toUpperCase());
		
		subscriptionService.subscribe(callbackUrl, hubMode, topic, leaseSeconds, secret);
		
		} catch (HubException e) {
			LOGGER.error("Error occured while processing request to " + mode + " for topic " + topic + " from subscriber's callback url " + callbackUrl);
			throw new RestException(e, e.getMessage() + e.getReason());
		} finally {
			LOGGER.info("Request to " + mode + " ended for topic " + topic + " from subscriber's callback url " + callbackUrl + ". Time taken (ms) = " + sw.getTime());
			sw.stop();
		}
	}

	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded", params = {HUB_MODE_PARAM, HUB_URL_PARAM} )
	@ResponseBody public void publish (
			@RequestParam(value = HUB_MODE_PARAM) String mode, 
			@RequestParam(value = HUB_URL_PARAM) String url)
			throws HubException {
		
		LOGGER.info("Request to " + mode + " started for resource " + url);
		StopWatch sw = new StopWatch();
		sw.start();
		try {
			// TODO: decode url values?
			List<String> errors = hubValidator.validatePing(mode, url);
			if (!errors.isEmpty()) {
				throw new HubException(ApplicationErrors.BAD_REQUEST, errors.toString());
			}
			
			contentDistributionService.distribute(url);
			
		} catch (HubException e) {
			LOGGER.error("Error occured while processing request to " + mode + " the resource " + url);
			throw new RestException(e, e.getMessage() + e.getReason());
		} finally {
			LOGGER.info("Request to " + mode + " ended for resource " + url + ". Time taken (ms) = " + sw.getTime());
			sw.stop();
		}
	}

	@ExceptionHandler(value = { RestException.class })
	@ResponseBody public HubError restExceptionHandler(RestException ex,
			HttpServletResponse response) {
		HubError error = new HubError();

		try {
			response.setStatus(ex.getHttpStatus().value());
			error.setErrorCode(String.valueOf(ex.getHubException()
					.getErrorCode()));
			error.setErrorMessage(ex.getHubException().getErrorMessage());
			error.setApplication(APP_NAME);

		} catch (Exception e) {
			// log
			// log
		}
		return error;
	}
}
