package org.motechproject.mds.test.domain.cascadedelete;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsVersionedEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Subject extends MdsVersionedEntity {

    @Field
    private String branch;

    @Field
    @Cascade(delete = true)
    private Set<Unit> units;


    public Subject(String branch) {
        this.branch = branch;
        this.units = new HashSet<>();
    }

    public String getBranch() {
        return branch;
    }

    public Set<Unit> getUnits() {
        return units;
    }

    public void setUnits(Set<Unit> units) {
        this.units = units;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Subject subject = (Subject) o;

        return !(branch != null ? !branch.equals(subject.branch) : subject.branch != null);

    }

    @Override
    public int hashCode() {
        return branch != null ? branch.hashCode() : 0;
    }
}
