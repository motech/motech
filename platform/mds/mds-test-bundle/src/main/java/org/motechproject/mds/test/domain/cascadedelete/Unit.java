package org.motechproject.mds.test.domain.cascadedelete;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsVersionedEntity;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Unit extends MdsVersionedEntity {

    @Field
    private String heading;

    @Field
    private Map<String, String> chapters;

    public Unit(String heading) {
        this.heading = heading;
        this.chapters = new HashMap<>();
    }

    public String getHeading() {
        return heading;
    }


    public Map<String, String> getChapters() {
        return chapters;
    }

    public void setChapters(Map<String, String> chapters) {
        this.chapters = chapters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Unit unit = (Unit) o;

        return !(heading != null ? !heading.equals(unit.heading) : unit.heading != null);

    }

    @Override
    public int hashCode() {
        return heading != null ? heading.hashCode() : 0;
    }
}
