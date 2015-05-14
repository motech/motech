package org.motechproject.mds.test.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mds.test.domain.ValidationSample;
import org.motechproject.mds.test.service.ValidationSampleService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.junit.Assert.assertNotNull;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MdsDdeValidationContextIT extends BasePaxIT {

    @Inject
    private ValidationSampleService validationSampleService;

    @Test
    public void shouldSaveValidObject() {
        ValidationSample obj = validObject();

        obj = validationSampleService.create(obj);

        assertNotNull(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRejectInvalidPattern() {
        ValidationSample obj = validObject();
        obj.setStringPattern("UPPERCASE");

        validationSampleService.create(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRejectValuesBelowMin() {
        ValidationSample obj = validObject();
        obj.setMinInt(3);

        validationSampleService.create(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRejectValuesAboveMax() {
        ValidationSample obj = validObject();
        obj.setMaxLong(20L);

        validationSampleService.create(obj);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRejectOneNameOnly() {
        ValidationSample obj = validObject();
        obj.setFirstName(null);

        validationSampleService.create(obj);
    }

    private ValidationSample validObject() {
        ValidationSample obj = new ValidationSample();

        obj.setMaxLong(7L);
        obj.setMinInt(15);
        obj.setStringPattern("lowercase");
        obj.setFirstName("First");
        obj.setLastName("Last");

        return obj;
    }
}
