package org.motechproject.mds.web.domain;

/**
 * The <code>GridFieldSelectionUpdate</code> contains data about a visible fields update.
 */
public class GridFieldSelectionUpdate {

    private String field;

    private GridSelectionAction action;

    public GridFieldSelectionUpdate() {}

    public GridFieldSelectionUpdate(String field, GridSelectionAction action) {
        this.field = field;
        this.action = action;
    }

    public GridSelectionAction getAction() {
        return action;
    }

    public void setAction(GridSelectionAction action) {
        this.action = action;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
