package org.motechproject.hub.service.impl;

import org.motechproject.http.agent.service.HttpAgent;
import org.motechproject.http.agent.service.Method;
import org.motechproject.hub.service.DistributionServiceDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * This class implements the methods in the interface
 * <code>DistributionServiceDelegate</code>
 * 
 * @author Anuranjan
 * 
 */
@Service(value = "distributionServiceDelegate")
public class DistributionServiceDelegateImpl implements
        DistributionServiceDelegate {

    private HttpAgent httpAgentImpl;

    @Value("${retry.count}")
    private String retryCount;

    public void setRetryCount(String retryCount) {
        this.retryCount = retryCount;
    }

    @Value("${retry.interval}")
    private String retryInterval;

    public void setRetryInterval(String retryInterval) {
        this.retryInterval = retryInterval;
    }

    public HttpAgent getHttpAgentImpl() {
        return httpAgentImpl;
    }

    public void setHttpAgentImpl(HttpAgent httpAgentImpl) {
        this.httpAgentImpl = httpAgentImpl;
    }

    @Autowired
    public DistributionServiceDelegateImpl(HttpAgent httpAgentImpl) {
        this.httpAgentImpl = httpAgentImpl;
    }

    @Override
    public ResponseEntity<String> getContent(String topicUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<String>("parameters",
                headers);
        return (ResponseEntity<String>) httpAgentImpl
                .executeWithReturnTypeSync(topicUrl, entity, Method.POST,
                        Integer.valueOf(retryCount),
                        Long.valueOf(retryInterval));
    }

    @Override
    public void distribute(String callbackUrl, String content,
            MediaType contentType, String topicUrl) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.add("Link",
                "<http://localhost:8080/motech-platform-hub/hub/>; rel=\"hub\", <"
                        + topicUrl + ">; rel=\"self\"");
        HttpEntity<String> entity = new HttpEntity<String>(content, headers);

        httpAgentImpl.execute(callbackUrl, entity, Method.POST);
    }
}
