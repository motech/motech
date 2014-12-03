package org.motechproject.mds.testutil.records;

import java.util.Objects;

public class RelatedClass {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof RelatedClass)) {
            return false;
        } else {
            RelatedClass that = (RelatedClass) o;
            return Objects.equals(id, that.id);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
