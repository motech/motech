package org.motechproject.mds.test.domain.cascadedelete;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsVersionedEntity;


@Entity
public class ChildCase extends MdsVersionedEntity {

    @Field
    private String name;

    public ChildCase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChildCase child = (ChildCase) o;

        return !(name != null ? !name.equals(child.name) : child.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
