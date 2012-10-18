package org.motechproject.openmrs.ws.resource.impl;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.mockito.Mock;
import org.motechproject.openmrs.ws.OpenMrsInstance;
import org.motechproject.openmrs.ws.RestClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class AbstractResourceImplTest {
    @Mock
    private RestClient client;

    @Mock
    private OpenMrsInstance instance; 

    private Gson gson = new GsonBuilder().create();
    
    protected String readJsonFromFile(String filename) throws IOException {
        Resource resouce = new ClassPathResource(filename);
        String json = IOUtils.toString(resouce.getInputStream());
        resouce.getInputStream().close();

        return json;
    }

    protected JsonElement stringToJsonElement(String expectedJson) {
        JsonElement expectedJsonObj = getGson().fromJson(expectedJson, JsonObject.class);
        return expectedJsonObj;
    }

    public RestClient getClient() {
        return client;
    }

    public Gson getGson() {
        return gson;
    }

    public OpenMrsInstance getInstance() {
        return instance;
    }
}
