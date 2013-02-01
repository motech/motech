package org.motechproject.commcare.service.impl;

import com.google.gson.reflect.TypeToken;
import org.motechproject.commcare.domain.CommcareFixture;
import org.motechproject.commcare.domain.CommcareFixturesJson;
import org.motechproject.commcare.service.CommcareFixtureService;
import org.motechproject.commcare.util.CommCareAPIHttpClient;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CommcareFixtureServiceImpl implements CommcareFixtureService {

    private MotechJsonReader motechJsonReader;

    private CommCareAPIHttpClient commcareHttpClient;

    @Autowired
    public CommcareFixtureServiceImpl(CommCareAPIHttpClient commcareHttpClient) {
        this.commcareHttpClient = commcareHttpClient;
        this.motechJsonReader = new MotechJsonReader();
    }

    @Override
    public List<CommcareFixture> getAllFixtures() {

        String response = commcareHttpClient.fixturesRequest();

        Type commcareFixtureType = new TypeToken<CommcareFixturesJson>() { } .getType();
        CommcareFixturesJson allFixtures = (CommcareFixturesJson) motechJsonReader.readFromString(response, commcareFixtureType);

        return allFixtures.getObjects();

    }

    @Override
    public CommcareFixture getCommcareFixtureById(String id) {
        String returnJson = commcareHttpClient.fixtureRequest(id);

        Type commcareFixtureType = new TypeToken<CommcareFixture>() { } .getType();
        CommcareFixture fixture = (CommcareFixture) motechJsonReader.readFromString(returnJson, commcareFixtureType);

        return fixture;
    }
}
