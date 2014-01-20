package org.motechproject.mds.service;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;

/**
 * This interface provides method to create a bundle jar with all entities defined in MDS module.
 */
public interface JarGeneratorService {

    File generate() throws IOException, NotFoundException, CannotCompileException;

}
