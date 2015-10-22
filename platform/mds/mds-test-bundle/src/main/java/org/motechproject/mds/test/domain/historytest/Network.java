package org.motechproject.mds.test.domain.historytest;

import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

import javax.jdo.annotations.Persistent;
import java.util.Iterator;
import java.util.List;

/**
 * Network and Computer are 1:N bidirectional
 */
@Entity(recordHistory = true)
public class Network extends MdsEntity {

    @Field
    private String mask;

    @Field
    @Persistent(mappedBy = "network")
    @Cascade(delete = true)
    private List<Computer> computers;

    public Network() {
    }

    public Network(String mask, List<Computer> computers) {
        this.mask = mask;
        this.computers = computers;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public List<Computer> getComputers() {
        return computers;
    }

    public void setComputers(List<Computer> computers) {
        this.computers = computers;
    }

    public void removeComputer(String name) {
        Iterator<Computer> cpuIt = getComputers().iterator();
        while (cpuIt.hasNext()) {
            Computer computer = cpuIt.next();
            if (name.equals(computer.getName())) {
                cpuIt.remove();
            }
        }
    }

    public Computer getComputerByName(String name) {
        for (Computer computer : getComputers()) {
            if (name.equals(computer.getName())) {
                return computer;
            }
        }
        return null;
    }
}
