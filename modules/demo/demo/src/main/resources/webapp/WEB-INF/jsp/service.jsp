
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>

<html>
<body>
<%
    List<String> services = (List<String>)request.getAttribute("services");
    if (services.isEmpty()) {
%>
    Any IVR Service not found
<%
    } else {
%>
    <form method="post">
        <select name="service">
<%
    Iterator<String> it = services.iterator();
    int index = 0;

    while (it.hasNext()) {
        String service = it.next();


        String current = (String) request.getAttribute("current");
        boolean selected = current == null ? false : current.equals(service);

        if (selected) {
%>
            <option value="<%= index %>" selected="selected"><%= service %></option>
<%
        } else {
%>
            <option value="<%= index %>"><%= service %></option>
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