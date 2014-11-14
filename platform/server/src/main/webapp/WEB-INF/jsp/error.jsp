<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title><spring:message code="server.error"/> ${errorCode}: MOTECH - <spring:message code="server.mobileTech"/></title>
        <link href="<%=request.getContextPath()%>/static/css/bootstrap.min.css" type="text/css" rel="stylesheet"/>
        <link href="<%=request.getContextPath()%>/static/css/bootstrap-page.css" type="text/css" rel="stylesheet"/>
    </head>

    <body>

        <div class="bodywrap">
            <div class="header">
                <div class="container">
                    <a href="<%=request.getContextPath()%>">
                        <div class="dashboard-logo"><img class="logo" alt="Logo - Mobile Technology for Community Health" src="<%=request.getContextPath()%>/static/img/motech-logo.jpg"></div>
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
                    <div class="navbar-inner navbar-inner-bg">
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
                    </p>
                    <c:if test="${not empty error}">
                        <p><spring:message code="server.error.exception"/></p>
                        <pre>${error}</pre>
                    </c:if>
                </div>
            </div>
        </div>

    </body>
</html>