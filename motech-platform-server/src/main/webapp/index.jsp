<%--

    MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT

    Copyright (c) 2012 Grameen Foundation USA.  All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    1. Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

    3. Neither the name of Grameen Foundation USA, nor its respective contributors
    may be used to endorse or promote products derived from this software without
    specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
    THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
    BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
    IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
    OF SUCH DAMAGE.

--%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
java.util.jar.Manifest manifest = new java.util.jar.Manifest();
manifest.read(pageContext.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF"));
java.util.jar.Attributes attributes = manifest.getMainAttributes();
org.motechproject.server.osgi.OsgiFrameworkService osgiService = org.motechproject.server.osgi.OsgiListener.getOsgiService();
%>

<html>
<body>
<h2>MOTECH - Mobile Technology for Community Health</h2>
<hr/>
<h3>Main module</h3>
<ul>
<li><b>Title:</b> <%=attributes.getValue("Implementation-Title")%>
<li><b>Version:</b> <%=attributes.getValue("Implementation-Version")%>
<li><b>Internal Bundle Folder:</b> <%=osgiService.getInternalBundleFolder()%>
<li><b>External Bundle Folder:</b> <%=osgiService.getExternalBundleFolder()%>
</ul>

<h3>Modules</h3>
<ul>
<%
    for ( org.motechproject.server.osgi.JarInformation jarInfo : osgiService.getBundledModules() ) {
        if ("org.motechproject".equals(jarInfo.getImplementationVendorID())) {
%>
            <li><b><%= jarInfo.getImplementationTitle() %></b>
            <ul>
                <li>Version: <b><%= jarInfo.getImplementationVersion() %></b>
                <li>Path: <%= jarInfo.getPath() %>
                <li>File: <%= jarInfo.getFilename() %>
            </ul>
<%
        }
    }
%>
</ul>

<h3>Bundles</h3>
<ul>
<%
    for ( org.motechproject.server.osgi.BundleInformation bundleInfo : osgiService.getExternalBundles() ) {
%>
        <li><b><%= bundleInfo.getBundleId() %></b>)
        [<%= bundleInfo.getState() %>]
        <b><%= bundleInfo.getSymbolicName() %></b> <%= bundleInfo.getVersion() %>
        (<%= bundleInfo.getLocation() %>)
<%
    }
%>
</ul>

<hr/>
<b>Generated at:</b> <%= new java.util.Date() %>
</body>
</html>
