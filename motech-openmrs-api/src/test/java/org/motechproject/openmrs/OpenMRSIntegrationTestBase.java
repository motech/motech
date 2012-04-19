package org.motechproject.openmrs;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.motechproject.openmrs.security.OpenMRSSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.Date;
import java.util.ResourceBundle;

import static org.motechproject.openmrs.TestIdGenerator.newGUID;
import static org.motechproject.openmrs.services.OpenMRSUserAdapter.USER_KEY;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationOpenmrsAPI.xml"})
@TransactionConfiguration(defaultRollback=true, transactionManager = "transactionManager")
public class OpenMRSIntegrationTestBase {
    @Autowired
    OpenMRSSession openMRSSession;

    @Autowired
    protected MRSUserAdapter userAdapter;

    @Autowired
    protected MRSPatientAdapter patientAdapter;

    @Autowired
    protected MRSFacilityAdapter facilityAdapter;

    boolean doOnce = false;

    @Before
    public final void setUpBefore() {
        setUp();
        if (!doOnce) {
            doOnceBefore();
            doOnce = true;
        }
    }

    public void doOnceBefore() {}

    public void setUp() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("openmrs");
        OpenMRSTestAuthenticationProvider.login(resourceBundle.getString("openmrs.admin.username"), resourceBundle.getString("openmrs.admin.password"));
        openMRSSession.open();
        openMRSSession.authenticate();
    }

    protected MRSPatient createPatient(MRSFacility facility) {
        final String first = "AlanTest";
        final String middle = "Wilkinson";
        final String last = "no";
        final String address1 = "a good street in ghana";
        final Date birthDate = new LocalDate(1970, 3, 11).toDate();
        final String gender = "M";
        Boolean birthDateEstimated = true;
        String patientSystemId = newGUID("10000045555");

        MRSPerson mrsPerson = new MRSPerson().firstName(first).lastName(last).middleName(middle).preferredName("prefName").
                birthDateEstimated(birthDateEstimated).dateOfBirth(birthDate).address(address1).gender(gender);
        final MRSPatient patient = new MRSPatient(patientSystemId, mrsPerson, facility);
        return patientAdapter.savePatient(patient);
    }


    protected MRSUser createUser(MRSUser userCreator) throws UserAlreadyExistsException {
        userCreator = (MRSUser) userAdapter.saveUser(userCreator).get(USER_KEY);
        return userCreator;
    }


    public void tearDown() {
        openMRSSession.close();
    }
}
