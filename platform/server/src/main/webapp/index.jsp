<%@ page import="org.motechproject.server.impl.OsgiListener" %>
<%
    if (OsgiListener.isBootstrapPresent() && OsgiListener.isServerBundleActive()) {
        response.sendRedirect("module/server/");
    } else if (OsgiListener.isErrorOccurred()) {
        response.sendRedirect("general-error.html");
    } else {
        response.sendRedirect("bootstrap/");
    }
%>