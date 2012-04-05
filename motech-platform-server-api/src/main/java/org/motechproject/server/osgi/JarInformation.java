/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2012 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.server.osgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarInformation {
    public static final String EXTRACTION_FAILED = "Extraction failed.";
    public static final String IMPLEMENTATION_VERSION = "Implementation-Version";
    public static final String IMPLEMENTATION_TITLE = "Implementation-Title";
    public static final String IMPLEMENTATION_VENDOR_ID = "Implementation-Vendor-Id";

    private String path;
    private String filename;
    private String implementationVendorID;
    private String implementationTitle;
    private String implementationVersion = null;

    public JarInformation(File file) throws IOException {
        readManifestInformation(file);
    }

    public void readManifestInformation(File file) throws IOException {
        if (file.isDirectory()) {
            readManifestFromDirectory(file);
        } else {
            readManifestFromJar(file);
        }
    }

    private void readManifestFromDirectory(File file) throws IOException {
        File manifestFile = new File(file, "META-INF/MANIFEST.MF");
        FileInputStream fis = new FileInputStream(manifestFile);

        Manifest manifest = new Manifest(fis);
        fis.close();

        getManifestData(file, manifest);
    }

    private void readManifestFromJar(File file) throws IOException {
        JarFile jarFile = new JarFile(file, false, JarFile.OPEN_READ);
        Manifest manifest = jarFile.getManifest();
        getManifestData(file, manifest);
    }

    private void getManifestData(File file, Manifest manifest) {
        path = file.getParent();
        filename = file.getName();

        if (manifest != null) {
            Attributes attributes = manifest.getMainAttributes();
            implementationVersion = attributes.getValue(IMPLEMENTATION_VERSION);
            implementationTitle = attributes.getValue(IMPLEMENTATION_TITLE);
            implementationVendorID = attributes.getValue(IMPLEMENTATION_VENDOR_ID);
        }
    }

    public String getImplementationVersion() {
        return (implementationVersion == null) ? EXTRACTION_FAILED : implementationVersion;
    }

    public String getImplementationTitle() {
        return implementationTitle;
    }

    public String getImplementationVendorID() {
        return implementationVendorID;
    }

    public String getPath() {
        return path;
    }

    public String getFilename() {
        return filename;
    }

}
