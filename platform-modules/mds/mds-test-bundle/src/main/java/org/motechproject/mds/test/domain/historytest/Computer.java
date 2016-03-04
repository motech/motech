package org.motechproject.mds.test.domain.historytest;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

/**
 * Network and Computer are 1:N bidirectional
 */
@Entity(recordHistory = true)
public class Computer extends MdsEntity {

    @Field
    private String name;

    @Field
    private Network network;

    public Computer() {
    }

    public Computer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
}
