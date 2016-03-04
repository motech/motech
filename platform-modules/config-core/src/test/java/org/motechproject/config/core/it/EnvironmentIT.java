package org.motechproject.config.core.it;

import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.config.core.environment.impl.EnvironmentImpl;

public class EnvironmentIT {

    private EnvironmentImpl environment;

    @Before
    public void setUp() {
        environment = new EnvironmentImpl();
    }

    @Test
    public void shouldFetchGivenEnvironmentVariableIfExists(){
        String path = "PATH";
        String value = environment.getValue(path);
        Assert.assertThat(value, IsNull.notNullValue());
    }

    @Test
    public void shouldReturnBlankStringWhenEnvironmentVariableDoesNotExist(){
        String path = "XYZ";
        String value = environment.getValue(path);
        Assert.assertEquals("", value);
    }
}
