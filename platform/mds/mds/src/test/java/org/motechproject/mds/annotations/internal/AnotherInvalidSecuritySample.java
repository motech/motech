package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.util.SecurityMode;

@Entity
@Access(value = SecurityMode.EVERYONE, members = {"motech"})
public class AnotherInvalidSecuritySample {
}
