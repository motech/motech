package org.motechproject.mobileforms.api.domain;

import org.fcitmuk.epihandy.ResponseHeader;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static junit.framework.Assert.assertTrue;

public class FormOutputTest {
    private FormOutput formOutput;

    @Before
    public void setUp() {
        formOutput = new FormOutput();
    }

    @Test
    public void shouldWriteSuccessCumErrorCounts() throws IOException {
        FormBean formBean1 = new FormBean();
        formBean1.setStudyName("study1");
        formBean1.setFormName("form1");
        formBean1.setXmlContent("xml1");
        FormBean formBean2 = new FormBean();
        formBean2.setStudyName("study2");
        formBean2.setFormName("form2");
        formBean2.setXmlContent("xml2");
        ByteArrayOutputStream actualByteStream = new ByteArrayOutputStream();

        formOutput.add(formBean1, Arrays.asList(new FormError("", "")));
        formOutput.add(formBean2, Arrays.asList(new FormError("", "")));
        formOutput.populate(new DataOutputStream(actualByteStream));

        ByteArrayOutputStream expectedByteStream = new ByteArrayOutputStream();
        DataOutputStream expectedDataOutputStream = new DataOutputStream(expectedByteStream);
        expectedDataOutputStream.writeByte(ResponseHeader.STATUS_SUCCESS);
        expectedDataOutputStream.writeInt(0);
        expectedDataOutputStream.writeInt(2);
        expectedDataOutputStream.writeByte(0);
        expectedDataOutputStream.writeShort(0);
        expectedDataOutputStream.writeUTF("xml2");

        expectedDataOutputStream.writeByte(1);
        expectedDataOutputStream.writeShort(0);
        expectedDataOutputStream.writeUTF("xml1");

        assertTrue(Arrays.equals(expectedByteStream.toByteArray(), actualByteStream.toByteArray()));

    }
}
