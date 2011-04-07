package org.motechproject.server.service.ivr;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 4/7/11
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class CallDetailRecord
{
    public enum Disposition {
	    ANSWERED, BUSY, FAILED, NO_ANSWER, UNKNOWN
    }

    private Date startDate;
    private Date endDate;
    private Date answerDate;
    private Disposition disposition;
    private Integer duration;
    private String errorMessage;

    public CallDetailRecord(Date startDate, Date endDate, Date answerDate,
                            Disposition disposition, Integer duration)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.answerDate = answerDate;
        this.disposition = disposition;
        this.duration = duration;
    }

    public CallDetailRecord(Disposition disposition, String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public Date getAnswerDate()
    {
        return answerDate;
    }

    public Disposition getDisposition()
    {
        return disposition;
    }

    public Integer getDuration()
    {
        return duration;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }
}
