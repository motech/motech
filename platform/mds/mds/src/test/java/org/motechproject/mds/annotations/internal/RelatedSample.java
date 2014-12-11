package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.RestOperations;
import org.motechproject.mds.domain.RestOperation;
import org.motechproject.mds.event.CrudEventType;

@Entity(recordHistory = true)
@RestOperations({RestOperation.READ, RestOperation.UPDATE, RestOperation.ALL, RestOperation.READ})
@CrudEvents({CrudEventType.ALL})
public class RelatedSample {

    private String testField;

    @Field
    private Sample oneToOneBi2;

    @Field
    private Sample manyToOneBi;

    public String getTestField() {
        return testField;
    }

    public void setTestField(String testField) {
        this.testField = testField;
    }

    public Sample getOneToOneBi2() {
        return oneToOneBi2;
    }

    public void setOneToOneBi2(Sample oneToOneBi2) {
        this.oneToOneBi2 = oneToOneBi2;
    }

    public Sample getManyToOneBi() {
        return manyToOneBi;
    }

    public void setManyToOneBi(Sample manyToOneBi) {
        this.manyToOneBi = manyToOneBi;
    }
}
