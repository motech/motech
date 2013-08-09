package org.motechproject.scheduler.domain;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;

/**
 * JobBasicInfoComparator is the implementation of Comparator interface,
 * which allows different outcome of compare(..) method. Thanks to this,
 * it is possible to sort JobBasicInfos based on single fields.
 *
 * @see JobBasicInfo
 */

public class JobBasicInfoComparator implements Comparator<JobBasicInfo> {
    private String compareField = "activity";
    private Boolean ascending = true;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("Y-MM-dd HH:mm:ss");

    public JobBasicInfoComparator(Boolean ascending, String compareField) {
        this.compareField = compareField;
        this.ascending = ascending;
    }

    @Override
    public int compare(JobBasicInfo o1, JobBasicInfo o2) {
        DateTime o1Time;
        DateTime o2Time;
        int ret;

        switch (compareField) {
            case "status":
                ret =  o1.getStatus().compareTo(o2.getStatus());
                break;
            case "name":
                ret =  o1.getName().compareTo(o2.getName());
                break;
            case "startDate":
                o1Time = dateTimeFormatter.parseDateTime(o1.getStartDate());
                o2Time = dateTimeFormatter.parseDateTime(o2.getStartDate());

                ret = o1Time.compareTo(o2Time);
                break;
            case "nextFireDate":
                o1Time = dateTimeFormatter.parseDateTime(o1.getNextFireDate());
                o2Time = dateTimeFormatter.parseDateTime(o2.getNextFireDate());

                ret = o1Time.compareTo(o2Time);
                break;
            case "endDate":
                ret = compareEndDate(o1, o2);
                break;
            case "jobType":
                ret = o1.getJobType().compareTo(o2.getJobType());
                break;
            case "activity":
            default:
                ret =  o1.getActivity().compareTo(o2.getActivity());
                break;
        }

        return (ascending) ? ret : -ret;
    }

    private int compareEndDate(JobBasicInfo o1, JobBasicInfo o2) {
        DateTime o1Time;
        DateTime o2Time;
        int ret;

        if ("-".equals(o1.getEndDate()) && "-".equals(o2.getEndDate())) {
            ret = 0;
        } else if ("-".equals(o1.getEndDate())) {
            ret = 1;
        } else if ("-".equals(o2.getEndDate())) {
            ret = -1;
        } else {
            o1Time = dateTimeFormatter.parseDateTime(o1.getEndDate());
            o2Time = dateTimeFormatter.parseDateTime(o2.getEndDate());

            ret = o1Time.compareTo(o2Time);
        }

        return ret;
    }
}
