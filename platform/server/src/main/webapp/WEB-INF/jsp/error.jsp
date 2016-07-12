<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title><spring:message code="server.error"/> ${errorCode}: MOTECH - <spring:message code="server.mobileTech"/></title>

        <%@ include file="header.jsp" %>

    </head>

    <body>

        <div class="bodywrap">
            <div class="header">
                <div class="container">
                    <a href="<%=request.getContextPath()%>">
                        <div class="dashboard-logo"><img class="logo" alt="Logo - Mobile Technology for Community Health" src="<%=request.getContextPath()%>/static/common/img/motech-logo.gif"></div>
                    </a>
                    <div class="hidden-xs hidden-sm">
                        <div class="header-title">
                            <spring:message code="server.mobileTech"/>
                        </div>
                    </div>
                    <div class="clearfix"></div>
                </div>
            </div>

            <div class="clearfix"></div>

            <div class="navbar-wrapper navbar-default">
                <div class="header-nav navbar error-code">
                    <div class="navbar-inner">
                        <div class="container">
                            <div class="col-md-12 error-content">
                                <h1><spring:message code="server.error"/> ${errorCode}: ${shortDesc}</h1>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="clearfix"></div>

            <div class="container">
                <div class="col-md-12 error-content">
                    <p>
                        ${longDesc}
                    </p>
                    <p>
                        <spring:message code="server.error.moreInfo"/>: <a href="http://docs.motechproject.org" target="_blank">docs.motechproject.org</a>
                    </p>
                    <c:if test="${noBootstrap}">
                        <p><b><spring:message code="server.error.noBootstrap"/></b></p>
                    </c:if>
                    <p>
                        <a class="btn btn-default" href="<%=request.getContextPath()%>"><spring:message code="server.error.goBack"/></a>
                        <a class="btn btn-default" href="<%=request.getContextPath()%>/server/j_spring_security_logout"><span class="fa fa-power-off"></span>&nbsp;<spring:message code="server.signOut"/></a>
                    </p>
                    <c:if test="${not empty bundleErrors || not empty contextErrors}">
                        <p><spring:message code="server.error.exception"/></p>
                        <c:if test="${not empty bundleErrors}">
                            <c:forEach var="error" items="${bundleErrors}">
                                <p><b>${error.key}:</b></p>
                                <pre style="white-space:pre-wrap;">${error.value}</pre>
                            </c:forEach>
                        </c:if>
                        <c:if test="${not empty contextErrors}">
                            <c:forEach var="error" items="${contextErrors}">
                                <p><b>${error.key}:</b></p>
                                <pre style="white-space:pre-wrap;">${error.value}</pre>
                            </c:forEach>
                        </c:if>
                    </c:if>
                </div>
            </div>
        </div>

    </body>
</html>