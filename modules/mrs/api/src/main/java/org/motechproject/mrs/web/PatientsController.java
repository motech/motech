package org.motechproject.mrs.web;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.exception.InvalidFacilityException;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.helper.MRSDtoHelper;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.util.ImplementationException;
import org.motechproject.mrs.util.ImplementationNotAvailableException;
import org.motechproject.mrs.util.MrsImplementationManager;
import org.motechproject.mrs.util.NoImplementationsAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Controller
public class PatientsController {

    @Autowired
    private MrsImplementationManager mrsImplementationManager;

    @RequestMapping(value = "/patients", method = RequestMethod.GET)
    @ResponseBody
    public List<MRSPatientDto> getPatients() throws ImplementationException {
        List<MRSPatient> patients = mrsImplementationManager.getPatientAdapter().getAllPatients();
        return MRSDtoHelper.createPatientDtoList(patients);
    }

    @RequestMapping(value = "/patients/req", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getRequired() throws ImplementationException {
        return mrsImplementationManager.getReqFieldsNames();
    }

    @RequestMapping(value = "/patients/{motechId}", method = RequestMethod.GET)
    @ResponseBody
    public MRSPatientDto getPatientById(@PathVariable String motechId)
            throws PatientNotFoundException, ImplementationException {

        MRSPatientAdapter patientAdapter = mrsImplementationManager.getPatientAdapter();
        return MRSDtoHelper.createPatientDto(patientAdapter.getPatientByMotechId(motechId));
    }

    @RequestMapping(value = "/patients/{motechId}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updatePatient(@PathVariable String motechId, @RequestBody MRSPatientDto patientDto)
            throws PatientNotFoundException, ImplementationException {

        validateFacility(patientDto);
        patientDto.setMotechId(motechId);
        mrsImplementationManager.getPatientAdapter().updatePatient(patientDto);
    }

    @RequestMapping(value = "/patients/{motechId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void savePatient(@PathVariable String motechId, @RequestBody MRSPatientDto patientDto)
            throws PatientNotFoundException, ImplementationException {

        validateFacility(patientDto);
        patientDto.setMotechId(motechId);
        mrsImplementationManager.getPatientAdapter().savePatient(patientDto);
    }

    @ExceptionHandler(InvalidFacilityException.class)
    public void handleInvalidFacility(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        try (Writer writer = response.getWriter()) {
            writer.write("key:mrs.noSuchFacility");
        }
    }

    @ExceptionHandler(NoImplementationsAvailableException.class)
    public void handleNoImplException(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        try (Writer writer = response.getWriter()) {
            writer.write("key:noImplAvail");
        }
    }


    @ExceptionHandler(ImplementationNotAvailableException.class)
    public void handleImplGoneException(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        try (Writer writer = response.getWriter()) {
            writer.write("key:implGone");
        }
    }

    @ExceptionHandler(Exception.class)
    public void handleMiscException(HttpServletResponse response, Exception ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        try (Writer writer = response.getWriter()) {
            writer.write(ExceptionUtils.getStackTrace(ex));
        }
    }

    private void validateFacility(MRSPatient patient) throws ImplementationException {
        if (patient.getFacility() != null) {
            String facilityId = patient.getFacility().getFacilityId();
            if (StringUtils.isNotBlank(facilityId) && !facilityExists(facilityId)) {
                throw new InvalidFacilityException();
            }
        }
    }

    private boolean facilityExists(String facilityId) throws ImplementationException {
        return mrsImplementationManager.getFacilityAdapter().getFacility(facilityId) != null;
    }
}
