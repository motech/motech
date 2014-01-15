<%@ page import="org.motechproject.server.impl.OsgiListener" %>
<%
    if (OsgiListener.isBootstrapPresent()) {
        response.sendRedirect("module/server/");
    } else {
        response.sendRedirect("bootstrap/");
    }
%>