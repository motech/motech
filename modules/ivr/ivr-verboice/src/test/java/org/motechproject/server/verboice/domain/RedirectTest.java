package org.motechproject.server.verboice.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class RedirectTest {

    @Test
    public void shouldGenerateRedirectTag(){
        Redirect redirect = new Redirect("http://url");
        assertEquals("<Redirect method=\"POST\">http://url</Redirect>",redirect.toXMLString());
    }
}
