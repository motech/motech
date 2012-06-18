package org.motechproject.commcare.domain;

/**
 * A Java object that encapsulates the actions
 * an implementer wants to take on a case. This
 * object is marshalled into case XML in order
 * to post to CommCareHQ.
 */
public class CaseTask {

    private CreateTask createTask;
    private UpdateTask updateTask;
    private IndexTask indexTask;
    private CloseTask closeTask;

    private String caseId;
    private String userId;
    private String dateModified;
    private String xmlns;

    public CreateTask getCreateTask() {
        return createTask;
    }

    public void setCreateTask(CreateTask createTask) {
        this.createTask = createTask;
    }

    public UpdateTask getUpdateTask() {
        return updateTask;
    }

    public void setUpdateTask(UpdateTask updateTask) {
        this.updateTask = updateTask;
    }

    public IndexTask getIndexTask() {
        return indexTask;
    }

    public void setIndexTask(IndexTask indexTask) {
        this.indexTask = indexTask;
    }

    public CloseTask getCloseTask() {
        return closeTask;
    }

    public void setCloseTask(CloseTask closeTask) {
        this.closeTask = closeTask;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

}