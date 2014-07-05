package org.motechproject.hub.validation;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * This is a test class which test the validator methods in
 * <code>HubValidator</code> class for publish/subscribe APIs
 * 
 * @author Anuranjan
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class HubValidatorTest {

    @InjectMocks
    private HubValidator hubValidator = new HubValidator();

    private String callbackUrl;
    private String mode;
    private String topic;
    private String leaseSeconds;
    private String secret;
    private String url;

    @Before
    public void setUp() {
        callbackUrl = "callback url";
        mode = "subscribe";
        topic = "/beneficiary";
        leaseSeconds = "20";
        secret = "secret";
        url = "topic url";
    }

    /**
     * Valid inputs scenario
     */
    @Test
    public void validateSubscriptionTest() {

        List<String> errors = hubValidator.validateSubscription(callbackUrl,
                mode, topic, leaseSeconds, secret);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    /**
     * Invalid scenario: mandatory fields empty and <code>leaseSeconds</code>
     * non-numeric
     */
    @Test
    public void validateSubscriptionWithEmptyStringsAndInvalidLeaseSeconds() {

        callbackUrl = "";
        mode = "";
        topic = "";
        leaseSeconds = "a";
        List<String> errors = hubValidator.validateSubscription(callbackUrl,
                mode, topic, leaseSeconds, secret);
        assertNotNull(errors);
        assertEquals(4, errors.size());
        assertEquals("hub.mode must be provided", errors.get(0));
        assertEquals("hub.callback must be provided", errors.get(1));
        assertEquals("hub.topic must be provided", errors.get(2));
        assertEquals(
                "hub.lease_seconds must be numeric. Provided value is [a]",
                errors.get(3));
    }

    /**
     * Invalid scenario: mandatory fields null and secret too long
     */
    @Test
    public void validateSubscriptionWithNullStrings() {

        callbackUrl = null;
        mode = null;
        topic = null;
        leaseSeconds = null;
        List<String> errors = hubValidator.validateSubscription(callbackUrl,
                mode, topic, leaseSeconds, secret);
        assertNotNull(errors);
        assertEquals(3, errors.size());
        assertEquals("hub.mode must be provided", errors.get(0));
        assertEquals("hub.callback must be provided", errors.get(1));
        assertEquals("hub.topic must be provided", errors.get(2));
    }

    /**
     * Invalid scenario: <code>mode</code> is invalid for subscription request
     */
    @Test
    public void validateSubscriptionWithInvalidModes() {

        callbackUrl = null;
        mode = "publish";
        topic = null;
        leaseSeconds = null;
        List<String> errors = hubValidator.validateSubscription(callbackUrl,
                mode, topic, leaseSeconds, secret);
        assertNotNull(errors);
        assertEquals(3, errors.size());
        assertEquals(
                "Invalid mode type [publish]. Supported modes: 'subscribe' or 'unsubscribe'",
                errors.get(0));
        assertEquals("hub.callback must be provided", errors.get(1));
        assertEquals("hub.topic must be provided", errors.get(2));
    }

    /**
     * Invalid scenario: Illegal <code>mode</code> type
     */
    @Test
    public void validateSubscriptionWithException() {

        callbackUrl = null;
        mode = "inValid";
        topic = null;
        leaseSeconds = null;
        List<String> errors = hubValidator.validateSubscription(callbackUrl,
                mode, topic, leaseSeconds, secret);
        assertNotNull(errors);
        assertEquals(3, errors.size());
        assertEquals(
                "hub.mode provided is not valid. Provided value is [inValid]",
                errors.get(0));
        assertEquals("hub.callback must be provided", errors.get(1));
        assertEquals("hub.topic must be provided", errors.get(2));
    }

    /**
     * Invalid scenario: <code>callback</code> and <code>topic</code> null and
     * <code>secret</code> too long
     */
    @Test
    public void validateSubscriptionWithValidModesAndOtherMandatoryFieldsNull() {

        callbackUrl = null;
        topic = null;
        leaseSeconds = null;
        secret = "A very long string  #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$ #$#$#$#$#$";
        List<String> errors = hubValidator.validateSubscription(callbackUrl,
                mode, topic, leaseSeconds, secret);
        assertNotNull(errors);
        assertEquals(3, errors.size());
        assertEquals(
                "hub.secret is too long. Maximum 200 bytes supported. Provided length is [206]",
                errors.get(0));
        assertEquals("hub.callback must be provided", errors.get(1));
        assertEquals("hub.topic must be provided", errors.get(2));

        mode = "unsubscribe";
        List<String> errors1 = hubValidator.validateSubscription(callbackUrl,
                mode, topic, leaseSeconds, secret);
        assertNotNull(errors1);
        assertEquals(2, errors1.size());
        assertEquals("hub.callback must be provided", errors1.get(0));
        assertEquals("hub.topic must be provided", errors1.get(1));
    }

    /**
     * Invalid scenario: <code>mode</code> and <code>topic</code> are null
     */
    @Test
    public void validateSubscriptionWithValidCallBackUrl() {

        mode = null;
        topic = null;
        leaseSeconds = null;
        List<String> errors = hubValidator.validateSubscription(callbackUrl,
                mode, topic, leaseSeconds, secret);
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("hub.mode must be provided", errors.get(0));
        assertEquals("hub.topic must be provided", errors.get(1));
    }

    /**
     * Invalid scenario: <code>callback</code> and <code>mode</code> are null
     */
    @Test
    public void validateSubscriptionWithValidTopic() {

        callbackUrl = null;
        mode = null;
        leaseSeconds = null;
        List<String> errors = hubValidator.validateSubscription(callbackUrl,
                mode, topic, leaseSeconds, secret);
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("hub.mode must be provided", errors.get(0));
        assertEquals("hub.callback must be provided", errors.get(1));
    }

    /**
     * Invalid scenario: <code>callback</code>, <code>mode</code> and
     * <code>topic</code> must not be null
     */
    @Test
    public void validateSubscriptionWithValidLeaseSeconds() {

        callbackUrl = null;
        mode = null;
        topic = null;
        List<String> errors = hubValidator.validateSubscription(callbackUrl,
                mode, topic, leaseSeconds, secret);
        assertNotNull(errors);
        assertEquals(3, errors.size());
        assertEquals("hub.mode must be provided", errors.get(0));
        assertEquals("hub.callback must be provided", errors.get(1));
        assertEquals("hub.topic must be provided", errors.get(2));
    }

    /**
     * Valid inputs scenario
     */
    @Test
    public void validatePingTest() {
        mode = "publish";
        List<String> errors = hubValidator.validatePing(mode, url);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    /**
     * Invalid scenario: <code>mode</code> and <code>url</code> must not be
     * empty
     */
    @Test
    public void validatePingWithEmptyStrings() {
        mode = "";
        url = "";
        List<String> errors = hubValidator.validatePing(mode, url);
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("hub.mode must be provided", errors.get(0));
        assertEquals("hub.url must be provided", errors.get(1));
    }

    /**
     * Invalid scenario: <code>mode</code> and <code>url</code> must be provided
     */
    @Test
    public void validatePingWithNullStrings() {
        mode = null;
        url = null;
        List<String> errors = hubValidator.validatePing(mode, url);
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("hub.mode must be provided", errors.get(0));
        assertEquals("hub.url must be provided", errors.get(1));
    }

    /**
     * Invalid scenario: Illegal <code>mode</code> type
     */
    @Test
    public void validatePingWithInvalidMode() {
        mode = "inValid";
        url = null;
        List<String> errors = hubValidator.validatePing(mode, url);
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals(
                "hub.mode provided is not valid. Provided value is [inValid]",
                errors.get(0));
        assertEquals("hub.url must be provided", errors.get(1));
    }

    /**
     * Invalid scenario: Unsupported <code>mode</code> type and missing
     * <code>url</code>
     */
    @Test
    public void validatePingWithValidMode() {
        url = null;
        List<String> errors = hubValidator.validatePing(mode, url);
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals(
                "Invalid mode type [subscribe]. Supported mode: 'publish'",
                errors.get(0));
        assertEquals("hub.url must be provided", errors.get(1));
    }

    /**
     * Invalid scenario: <code>mode</code> must be provided
     */
    @Test
    public void validatePingWithValidUrl() {
        mode = null;
        List<String> errors = hubValidator.validatePing(mode, url);
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("hub.mode must be provided", errors.get(0));
    }

    /**
     * Invalid scenario: Unsupported <code>mode</code> type
     */
    @Test
    public void validatePingWithValidParams() {
        mode = "unsubscribe";
        List<String> errors = hubValidator.validatePing(mode, url);
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(
                "Invalid mode type [unsubscribe]. Supported mode: 'publish'",
                errors.get(0));
    }
}
