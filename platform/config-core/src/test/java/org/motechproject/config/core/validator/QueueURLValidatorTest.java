package org.motechproject.config.core.validator;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.config.core.exception.MotechConfigurationException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class QueueURLValidatorTest {

    private static final List<String> INVALID_COMPOSITE_URLS = Arrays.asList("failoverr:(tcp://127.0.0.1:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100",
            "failover:(tcp://256.0.0.1:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100", "failover:(tcp://127.0..0.1:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100",
            "failover:((tcp:///127.0.0.1:61616,tcp://127.0.0.1:61616))?initialReconnectDelay=100", "failover:(tcp://127.0.0.1:61616,tcp://127.0.0.1:612616)?initialReconnectDelay=100",
            "failover:(tcp://127.0.0.1:61616,tcp://1217.0.0.1:61616)?initialReconnectDelay=100", "failover://(tcp://137.0.0.1:61616,tcp://137.0.0.1:61616)?timeout=3000",
            "fanout:(static:(tcp:///localhost:61629,tcp://localhost:61639,tcp://localhost:61649))", "fanout:(staatic:(tcp://localhost:61629,tcp://localhost:61639,tcp://localhost:61649))",
            "vm:(brooker:(tcp://localhost:6000)?persistent=false)?marshal=false", "wjfwwfeweffwwewf", "  ", ".....");

    private static final List<String> VALID_COMPOSITE_URLS = Arrays.asList("failover:(tcp://127.0.0.1:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100", "failover:(tcp://117.0.0.1:61616,tcp://117.0.0.1:61616)?randomize=false",
            "fanout:(static:(tcp://127.0.0.1:61629,tcp://127.0.0.1:61639,tcp://127.0.0.1:61649))", "failover:(tcp://192.168.42.100:61616,tcp://192.168.42.101:61616)",
            "failover:(tcp://137.0.0.1:61616,udp://137.0.0.1:61616)?randomize=false&priorityBackup=true", "vm:(broker:(tcp://127.0.0.1:6000)?persistent=false)?marshal=false",
            "failover:(vm://137.0.0.1:61616,http://137.0.0.1:61616,https://137.0.0.1:61616)?randomize=false&priorityBackup=true&priorityURIs=tcp://137.0.0.1:61616,tcp://137.0.0.1:61616",
            "vm:(static:(tcp://137.0.0.1:6000)?persistent=false)?marshal=false", "ssl://137.0.0.1:61616?transport.enabledCipherSuites=SSL_RSA_WITH_RC4_128_SHA,SSL_DH_anon_WITH_3DES_EDE_CBC_SHA");


    private QueueURLValidator queueURLValidator;

    @Before
    public void before() {
        queueURLValidator = new QueueURLValidator();
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldRejectQueueUrl() {
            queueURLValidator.validate("");
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldRejectInvalidQueueUrl() {
        queueURLValidator.validate("some.bad.url");
    }

    @Test
    public void shouldAcceptValidQueueURL() {
        queueURLValidator.validate("tcp://localhost:61616");
    }

    @Test
    public void shouldAcceptValidQueueLocalURL() {
        queueURLValidator.validate("http://activemq:61616");
    }

    @Test
    public void shouldAcceptValidQueueURLWithFQDN() {
        queueURLValidator.validate("tcp://some.good.url.com:61616");
    }

    @Test
    public void shouldAcceptValidCompositeQueueURLs() {
        for (String validCompositeUrl : VALID_COMPOSITE_URLS) {
            queueURLValidator.validate(validCompositeUrl);
        }
    }

    @Test
    public void shouldRejectInvalidCompositeQueueURLs() {
        int errors = 0;
        for (String invalidCompositeUrl : INVALID_COMPOSITE_URLS) {
            try {
                queueURLValidator.validate(invalidCompositeUrl);
            } catch (MotechConfigurationException e) {
                errors++;
            }
        }
        assertTrue(errors == INVALID_COMPOSITE_URLS.size());
    }
}
