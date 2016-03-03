package org.motechproject.mds.builder;

import javax.jdo.annotations.Persistent;
import java.util.Set;

public class Sample {

    private Long id;
    private Set<Sample> oneToManyName;
    private Sample oneToOneName;

    @Persistent(defaultFetchGroup = "false")
    private Sample notInDefFg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Sample> getOneToManyName() {
        return oneToManyName;
    }

    public void setOneToManyName(Set<Sample> oneToManyName) {
        this.oneToManyName = oneToManyName;
    }

    public Sample getOneToOneName() {
        return oneToOneName;
    }

    public void setOneToOneName(Sample oneToOneName) {
        this.oneToOneName = oneToOneName;
    }

    public Sample getNotInDefFg() {
        return notInDefFg;
    }

    public void setNotInDefFg(Sample notInDefFg) {
        this.notInDefFg = notInDefFg;
    }
}
