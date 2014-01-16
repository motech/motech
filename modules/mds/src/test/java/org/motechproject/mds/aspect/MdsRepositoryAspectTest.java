package org.motechproject.mds.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.repository.AllEntityMappings;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsRepositoryAspectTest {
    private MdsRepositoryAspect aspect = new MdsRepositoryAspect();

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfTargetNotExtendBaseMdsRepository() throws Throwable {
        when(joinPoint.getTarget()).thenReturn(new Object());

        aspect.execute(joinPoint);
    }

    @Test
    public void shouldRestoreOldChangeClassLoaderAfterMethodIsCompleted() throws Throwable {
        AllEntityMappings mappings = new AllEntityMappings();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        when(joinPoint.getTarget()).thenReturn(mappings);

        aspect.execute(joinPoint);

        Assert.assertEquals(classLoader, Thread.currentThread().getContextClassLoader());
    }
}
