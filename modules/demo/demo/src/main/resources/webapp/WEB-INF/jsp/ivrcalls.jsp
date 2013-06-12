<%
    String message = (String) request.getAttribute("message");
    String phoneNumber = (String) request.getAttribute("phoneNumberCall");
    if(message == "true") {
%>
    <%
        if(phoneNumber != "undefined") {
    %>
            <script type="text/javascript">
                location="<%=(String) request.getAttribute("callUrl")%>"+"#msgcall="+"<%=phoneNumber%>";
            </script>
    <% } else { %>
        <script type="text/javascript">
           location="<%=(String) request.getAttribute("callUrl")%>"+"#msg=true";
        </script>
    <%
      }
    %>
<%
   }
%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${pageLang}" />
<fmt:setBundle basename="org.motechproject.resources.messages" var="bundle"/>
<link rel="stylesheet" type="text/css" href="../../server/resources/css/index.css" />
<link rel="stylesheet" type="text/css" href="../../server/resources/css/bootstrap.css"/>
<script type="text/javascript" src="../../server/resources/lib/jquery/jquery-1.8.2.js"></script>
<html>
<body>
<div class="well2 ivr-panel">
    <div class="box-header"><fmt:message key="ivr" bundle="${bundle}"/></div>
    <div class="box-content clearfix">
    <%
        List<String> services = (List<String>)request.getAttribute("services");
        if (services.isEmpty()) {
    %>
        Any IVR Service not found
    <%
        } else {
    %>
        <form class="form-horizontal inside" method="post" action="/motech-platform-server/module/demo/api/ivrcalls" id="callForm">
            <div class="control-group">
                <label class="control-label" for="ivrSelect"><fmt:message key="ivr" bundle="${bundle}"/></label>
                <div class="controls">
                    <select id="ivrSelect" name="service">
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
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="phoneNumber">
                    <fmt:message key="ivr.phone.number" bundle="${bundle}"/>
                </label>
                <div class="controls">
                    <input type="text" name="phoneNumber" id="phoneNumber" />
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="delay">
                    <fmt:message key="ivr.delay" bundle="${bundle}"/>
                </label>
                <div class="controls">
                    <input type="text" name="delay" id="delay" placeholder="0" />
                </div>
            </div>
            <div class="controls">
                <input class="btn btn-primary" type="submit" value='<fmt:message key="ivr.call" bundle="${bundle}"/>'/>
            </div>

            <script type="text/javascript">
                $('#callForm').append('<input type="hidden" name="callUrl" value="'+location+'" />');
            </script>
        </form>
    <%
        }
    %>

    <script type="text/javascript">
       var urlValue= location.href;
       var urlTabCall = urlValue.split("#msgcall=");
       var urlTabMsg = urlValue.split("#msg");
       if(urlTabCall.length > 1) {
            alert("Making a call now to number "+urlTabCall[1]);
            location.href = urlTabCall[0];
       }else {
            if(urlTabMsg.length > 1) {
                alert("Phone number is undefined!");
                location.href = urlTabMsg[0];
            }
       }
    </script>
    </div>
</div>
</body>
</html>