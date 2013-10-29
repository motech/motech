package org.motechproject.config.core.bootstrap.impl;

import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    public void shouldReturnNullWhenEnvironmentVariableDoesNotExist(){
        String path = "XYZ";
        String value = environment.getValue(path);
        Assert.assertThat(value, IsNull.nullValue());
    }
}
