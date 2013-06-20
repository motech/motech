package org.motechproject.commcare.domain;

import com.google.gson.reflect.TypeToken;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.motechproject.commons.api.json.MotechJsonReader;

import java.lang.reflect.Type;

import static org.junit.Assert.assertThat;


public class CaseResponseTest {

    @Test
    public void shouldParseCaseResponseJson() {
        Type commcareCaseType = new TypeToken<CaseResponseJson>() {
        }.getType();
        MotechJsonReader motechJsonReader = new MotechJsonReader();
        CaseResponseJson caseResponse = (CaseResponseJson) motechJsonReader.readFromFile("/json/domain/cases.json", commcareCaseType);
        assertThat(caseResponse.getMetadata().getTotalCount(), IsEqual.equalTo(4));
        assertThat(caseResponse.getCases().size(), IsEqual.equalTo(2));
    }
}
