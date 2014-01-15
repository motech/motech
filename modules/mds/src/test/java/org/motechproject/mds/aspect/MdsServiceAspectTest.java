package org.motechproject.mds.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.impl.EntityServiceImpl;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsServiceAspectTest {
    private MdsServiceAspect aspect = new MdsServiceAspect();

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfTargetNotExtendBaseMdsService() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(new Object());

        aspect.makeChange(joinPoint);
    }

    @Test
    public void shouldRestoreOldChangeClassLoaderAfterMethodIsCompleted() throws Throwable {
        EntityService service = new EntityServiceImpl();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        when(joinPoint.getTarget()).thenReturn(service);

        aspect.makeChange(joinPoint);

        Assert.assertEquals(classLoader, Thread.currentThread().getContextClassLoader());
    }
}
