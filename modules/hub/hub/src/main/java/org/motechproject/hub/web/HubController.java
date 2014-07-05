package org.motechproject.hub.web;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.motechproject.hub.exception.ApplicationErrors;
import org.motechproject.hub.exception.HubError;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.exception.RestException;
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
public class HubController {

    private static final Logger LOGGER = Logger.getLogger(HubController.class);

    @Autowired
    private HubValidator hubValidator;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private ContentDistributionService contentDistributionService;

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
        return subscriptionService.sayHello();
    }

    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded", params = {
            HubConstants.HUB_CALLBACK_PARAM, HubConstants.HUB_MODE_PARAM,
            HubConstants.HUB_TOPIC_PARAM })
    @ResponseBody
    public void subscribe(
            @RequestParam(value = HubConstants.HUB_CALLBACK_PARAM) String callbackUrl,
            @RequestParam(value = HubConstants.HUB_MODE_PARAM) String mode,
            @RequestParam(value = HubConstants.HUB_TOPIC_PARAM) String topic,
            @RequestParam(value = HubConstants.HUB_LEASE_SECONDS_PARAM, required = false) String leaseSeconds,
            @RequestParam(value = HubConstants.HUB_SECRET_PARAM, required = false) String secret)
            throws HubException {

        LOGGER.info(String
                .format("Request to %s started for topic %s from subscriber's callback url '%s'",
                        mode, topic, callbackUrl));
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            List<String> errors = hubValidator.validateSubscription(
                    callbackUrl, mode, topic, leaseSeconds, secret);
            if (!errors.isEmpty()) {
                throw new HubException(ApplicationErrors.BAD_REQUEST,
                        errors.toString());
            }
            Modes hubMode = Modes.valueOf(mode.toUpperCase());

            subscriptionService.subscribe(callbackUrl, hubMode, topic,
                    leaseSeconds, secret);

        } catch (HubException e) {
            LOGGER.error("Error occured while processing request to " + mode
                    + " for topic " + topic
                    + " from subscriber's callback url " + callbackUrl);
            throw new RestException(e, e.getMessage() + e.getReason());
        } finally {
            LOGGER.info(String
                    .format("Request to %s ended for topic %s from subscriber's callback url '%s'. Time taken (ms) = %d",
                            mode, topic, callbackUrl, sw.getTime()));
            sw.stop();
        }
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded", params = {
            HubConstants.HUB_MODE_PARAM, HubConstants.HUB_URL_PARAM })
    @ResponseBody
    public void publish(
            @RequestParam(value = HubConstants.HUB_MODE_PARAM) String mode,
            @RequestParam(value = HubConstants.HUB_URL_PARAM) String url)
            throws HubException {

        LOGGER.info(String.format("Request to %s started for resource %s",
                mode, url));
        StopWatch sw = new StopWatch();
        sw.start();
        try {
            List<String> errors = hubValidator.validatePing(mode, url);
            if (!errors.isEmpty()) {
                throw new HubException(ApplicationErrors.BAD_REQUEST,
                        errors.toString());
            }

            contentDistributionService.distribute(url);

        } catch (HubException e) {
            LOGGER.error("Error occured while processing request to " + mode
                    + " the resource " + url);
            throw new RestException(e, e.getMessage() + e.getReason());
        } finally {
            LOGGER.info(String
                    .format("Request to %s ended for resource %s. Time taken (ms) = %d",
                            mode, url, sw.getTime()));
            sw.stop();
        }
    }

    @ExceptionHandler(value = { RestException.class })
    @ResponseBody
    public HubError restExceptionHandler(RestException ex,
            HttpServletResponse response) {
        HubError error = new HubError();

        response.setStatus(ex.getHttpStatus().value());
        error.setErrorCode(String.valueOf(ex.getHubException().getErrorCode()));
        error.setErrorMessage(ex.getHubException().getErrorMessage());
        error.setApplication(HubConstants.APP_NAME);

        return error;
    }
}
