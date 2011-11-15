package org.motechproject.openmrs.omod.web.servlet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.openmrs.omod.service.OmodIdentifierService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class OmodIdentifierServletTest {
    private static final String ID_GENERATOR = "IdGenerator";
    private static final String ID_TYPE = "idType";

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private OmodIdentifierServlet omodIdentifierServlet;
    @Mock
    private OmodIdentifierService omodIdentifierService;
    @Mock
    private Context mockContext;


    @Before
    public void setUp() {
        initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        omodIdentifierServlet = spy(new OmodIdentifierServlet());
        doReturn(omodIdentifierService).when(omodIdentifierServlet).getOmodIdentifierService();
    }

    @Test
    public void shouldGenerateIDForRequestedIDTypeUsingGivenGenerator() throws IOException, ServletException {
        final String idGenerator = "MoTeCH Staff ID Generator";
        final String idType = "MoTeCH Staff Id";
        final String expectedId = "12345";
        request.addParameter(ID_TYPE,idType);
        request.addParameter(ID_GENERATOR,idGenerator);

        when(omodIdentifierService.getIdFor(idGenerator,idType)).thenReturn(expectedId);
        omodIdentifierServlet.doGet(request,response);

        assertThat(expectedId, is(equalTo(response.getContentAsString())));
    }

}
