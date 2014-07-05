package org.motechproject.batch.validation;

import java.util.ArrayList;
import java.util.List;

import org.quartz.CronExpression;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class BatchValidator {

    public List<String> validateShedulerInputs(String jobName,
            String cronExpression) {
        List<String> errors = new ArrayList<String>();
        checkJobName(jobName, errors);

        checkCronExpression(cronExpression, errors);
        return errors;
    }

    public List<String> validateOneTimeInputs(String jobName, String date) {

        List<String> errors = new ArrayList<String>();
        checkJobName(jobName, errors);
        return errors;
    }

    public List<String> validateUpdateInputs(String jobName) {

        List<String> errors = new ArrayList<String>();
        checkJobName(jobName, errors);
        return errors;
    }

    public List<String> validateUploadInputs(String jobName, String contentType) {

        List<String> errors = new ArrayList<String>();
        checkJobName(jobName, errors);

        checkContentType(contentType, errors);
        return errors;
    }

    private void checkContentType(String contentType, List<String> errors) {
        if (!MediaType.TEXT_XML.equals(MediaType.valueOf(contentType))) {
            errors.add("You must upload xml file for the job");
        }

    }

    private void checkCronExpression(String cronExpression, List<String> errors) {
        if (!CronExpression.isValidExpression(cronExpression)
                || cronExpression == null) {
            errors.add("Job cron expression supplied is not valid");
        }
    }

    private void checkJobName(String jobName, List<String> errors) {
        if (jobName == null || ("").equals(jobName)) {
            errors.add("Job name must be provided");
        }
    }

}
