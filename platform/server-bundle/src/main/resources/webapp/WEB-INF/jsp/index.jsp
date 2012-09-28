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

<h3>Bundles (<%= osgiService.getExternalBundles().size() %>)</h3>
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
