
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.motechproject.ivr.service.IVRService" %>

<html>
<body>
<%
    List<IVRService> services = (List<IVRService>)request.getAttribute("services");
    if (services.isEmpty()) {
%>
    Any IVR Service not found
<%
    } else {
%>
    <form method="post">
        <select name="service">
<%
    Iterator<org.motechproject.ivr.service.IVRService> it = services.iterator();
    int index = 0;

    while (it.hasNext()) {
        org.motechproject.ivr.service.IVRService service = it.next();

        int dot = service.toString().lastIndexOf('.');
        int at = service.toString().lastIndexOf('@');
        String name = service.toString().substring(dot + 1, at);

        IVRService current = (IVRService) request.getAttribute("current");
        boolean selected = current == null ? false : current == service;

        if (selected) {
%>
            <option value="<%= index %>" selected="selected"><%= name %></option>
<%
        } else {
%>
            <option value="<%= index %>"><%= name %></option>
<%
        }

        ++index;
    }
%>

        </select>
        <input type="submit" value="Send" />
    </form>
<%
    }
%>
</body>
</html>