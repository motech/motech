package org.motechproject.server.web.validator;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QueueURLValidatorTest {

    private static final String ERROR_INVALID = "server.error.invalid.%s";
    private static final String ERROR_REQUIRED = "server.error.required.%s";
    private static final String QUEUE_URL = "queueUrl";

    private static final List<String> INVALID_COMPOSITE_URLS = Arrays.asList("failoverr:(tcp://127.0.0.1:61616,tcp://127.0.0.1:61616)?initialReconnectDelay=100", "failover:(tcp://localhost:61616,tcp://remotehost:61616)?initialReconnectDelay=100",
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

    @Test
    public void shouldRejectQueueUrl() {
        Errors errors = mock(Errors.class);
        when(errors.getFieldValue(QUEUE_URL)).thenReturn("");

        queueURLValidator.validate(new StartupForm(), errors);

        verify(errors).rejectValue(QUEUE_URL, String.format(ERROR_REQUIRED, QUEUE_URL), null, null);
    }

    @Test
    public void shouldRejectInvalidQueueUrl() {
        Errors errors = mock(Errors.class);
        when(errors.getFieldValue(QUEUE_URL)).thenReturn("some.krap.url");

        queueURLValidator.validate(new StartupForm(), errors);

        verify(errors).rejectValue(QUEUE_URL, String.format(ERROR_INVALID, QUEUE_URL), null, null);
    }

    @Test
    public void shouldAcceptValidQueueURL() {
        Errors errors = mock(Errors.class);
        when(errors.getFieldValue(QUEUE_URL)).thenReturn("tcp://localhost:61616");

        queueURLValidator.validate(new StartupForm(), errors);

        verify(errors, never()).rejectValue(QUEUE_URL, String.format(ERROR_INVALID, QUEUE_URL), null, null);
    }

    @Test
    public void shouldAcceptValidCompositeQueueURLs() {
        for (String validCompositeUrl : VALID_COMPOSITE_URLS) {
            Errors errors = mock(Errors.class);
            when(errors.getFieldValue(QUEUE_URL)).thenReturn(validCompositeUrl);
            queueURLValidator.validate(new StartupForm(), errors);
            verify(errors, never()).rejectValue(QUEUE_URL, String.format(ERROR_INVALID, QUEUE_URL), null, null);
        }
    }

    @Test
    public void shouldRejectInvalidCompositeQueueURLs() {
        for (String invalidCompositeUrl : INVALID_COMPOSITE_URLS) {
            Errors errors = mock(Errors.class);
            when(errors.getFieldValue(QUEUE_URL)).thenReturn(invalidCompositeUrl);
            queueURLValidator.validate(new StartupForm(), errors);
            verify(errors).rejectValue(QUEUE_URL, String.format(ERROR_INVALID, QUEUE_URL), null, null);
        }
    }
}
