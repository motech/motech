package org.motechproject.mds.builder;

import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.Entity;

import java.util.List;

/**
 * An enum builder is responsible for building the enum class with the same values as those are
 * defined in the field.
 */
public interface EnumBuilder {

    List<ClassData> build(Entity entity);

}
