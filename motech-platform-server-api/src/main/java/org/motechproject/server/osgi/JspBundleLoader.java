/*
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
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

public class JspBundleLoader implements BundleLoader, ServletContextAware {

	private static Logger logger = LoggerFactory.getLogger(JspBundleLoader.class);

	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadBundle(Bundle bundle) throws Exception {
		Enumeration<URL> jsps = bundle.findEntries("/webapp", "*.jsp", true);
		if (jsps != null) {
			while (jsps.hasMoreElements()) {
				URL jspUrl = jsps.nextElement();

				String destFilename = buildDestFilename(jspUrl, bundle.getBundleId());
				File destFile = new File(destFilename);

				FileUtils.copyURLToFile(jspUrl, destFile);
				logger.debug("Loaded " + jspUrl.getFile() + " from [" + bundle.getLocation() + "]");
			}
		}
	}

	private String buildDestFilename(URL jspUrl, long bundleId) {
		String path = servletContext.getRealPath("/");
		String filename = jspUrl.getFile();
		StringBuilder sb = new StringBuilder();

		sb.append(path);
		if (!path.endsWith(File.separator)) {
			sb.append(File.separator);
		}

		sb.append(bundleId).append(File.separator);

		if (filename.startsWith(File.separator)) {
			sb.append(filename.substring(1));
		} else {
			sb.append(filename);
		}

		return sb.toString();
	}
}
