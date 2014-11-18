<%@ page import="org.motechproject.server.impl.OsgiListener" %>
<%
    if (OsgiListener.isBootstrapPresent() && OsgiListener.isServerBundleActive()) {
        response.sendRedirect("module/server/");
    } else if (OsgiListener.isErrorOccurred()) {
        response.sendRedirect("bootstrap/error/startup");
    } else {
        response.sendRedirect("bootstrap/");
    }
%>