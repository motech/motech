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
<html>
<body>
<%
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
</body>
</html>