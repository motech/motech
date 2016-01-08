<%@ page import="org.motechproject.server.impl.OsgiListener" %>
<%
    if (OsgiListener.isBootstrapPresent() && OsgiListener.isServerBundleActive()) {
        response.sendRedirect("module/server/");
    } else if (OsgiListener.inFatalError()) {
        response.sendRedirect("bootstrap/error/startup");
    } else {
        response.sendRedirect("bootstrap/");
    }
%>