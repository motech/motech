<%@ page import="org.motechproject.server.impl.OsgiListener" %>
<%
    if (OsgiListener.isBootstrapPresent() && OsgiListener.isServerBundleActive()) {
        response.sendRedirect("server/");
    } else if (OsgiListener.inFatalError()) {
        response.sendRedirect("server/error/startup");
    } else {
        response.sendRedirect("server/bootstrap/");
    }
%>