package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.ReadAccess;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.util.SecurityMode;

@Entity
@ReadAccess(value = SecurityMode.PERMISSIONS, members = {"manageEbodac"})
public class ReadAccessSample {

    private int someInt;
}
