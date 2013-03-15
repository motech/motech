package org.motechproject.event.utils;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsSame;
import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListenerAbstractProxy;

import static org.junit.Assert.assertThat;

public class MotechProxyUtilsTest {


    @Test
    public void shouldDetectProxy() {
        assertThat(MotechProxyUtils.isMotechListenerProxy(new TestProxy(new Object())), Is.is(true));
    }

    @Test
    public void shouldNotDetectProxy() {
        boolean isProxy = MotechProxyUtils.isMotechListenerProxy(new Object());
        assertThat(isProxy, Is.is(false));
    }


    @Test
    public void shouldReturnProxyTarget() {
        Object bean = new Object();
        TestProxy proxy = new TestProxy(bean);
        Object target = MotechProxyUtils.getTargetIfProxied(proxy);
        assertThat(target, IsSame.sameInstance(bean));
    }

    private class TestProxy extends MotechListenerAbstractProxy {

        public TestProxy(Object bean) {
            super("testBean", bean, null);
        }

        @Override
        public void callHandler(MotechEvent event) {

        }
    }

}
