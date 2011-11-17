package org.motechproject.openmrs.advice;

import org.junit.Ignore;
import org.junit.Test;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({OpenMRSSession.class, SecurityContextHolder.class})
public class LoginAsAdminAdviceTest {

    @Test
    @Ignore
    public void shouldAutenticateAsAdminAndInvokeTheCallee() throws Throwable {
//        mockStatic(OpenMRSSession.class);
//        mockStatic(SecurityContextHolder.class);
//        String userName = "userName";
//        String password = "password";
//        LoginAsAdminAdvice loginAsAdminAdvice = new LoginAsAdminAdvice();
//        ReflectionTestUtils.setField(loginAsAdminAdvice, "userName", userName);
//        ReflectionTestUtils.setField(loginAsAdminAdvice, "password", password);
//
//        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
//        Object expectedObjectToBeReturned = new Object();
//        when(pjp.proceed()).thenReturn(expectedObjectToBeReturned);
//
//        OpenMRSSecurityUser openMRSSecurityUser = mock(OpenMRSSecurityUser.class);
//        PowerMockito.when(OpenMRSSession.login(userName, password)).thenReturn(openMRSSecurityUser);
//
//        SecurityContext context = mock(SecurityContext.class);
//        PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(context);
//
//        Object actualObjectReturned = loginAsAdminAdvice.loginAsAdminAndInvoke(pjp);
//        ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationTokenArgumentCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
//        verify(context, times(2)).setAuthentication(authenticationTokenArgumentCaptor.capture());
//
//        List<UsernamePasswordAuthenticationToken> adminCredentials = authenticationTokenArgumentCaptor.getAllValues();
//        assertThat(((OpenMRSSecurityUser)adminCredentials.get(0).getPrincipal()), is(equalTo(openMRSSecurityUser)));
//        assertThat(adminCredentials.get(1), is(equalTo(null)));
//
//        assertThat(actualObjectReturned, is(expectedObjectToBeReturned));

    }
}
