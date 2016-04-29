package org.motechproject.mds.test.domain.cascadedelete;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsVersionedEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
public class MotherCase extends MdsVersionedEntity {

    @Field
    private String name;

    @Field
    @Cascade(delete = true)
    private List<ChildCase> childCases;


    public MotherCase(String name) {
        this.name = name;
        this.childCases = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<ChildCase> getChildCases() {
        return childCases;
    }

    public void setChildCases(List<ChildCase> childCases) {
        this.childCases = childCases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MotherCase mother = (MotherCase) o;

        return !(name != null ? !name.equals(mother.name) : mother.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
