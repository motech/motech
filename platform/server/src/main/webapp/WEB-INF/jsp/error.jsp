<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title><spring:message code="server.error"/> ${errorCode}: MOTECH - <spring:message code="server.mobileTech"/></title>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/bootstrap-page.css">
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/font-awesome.min.css">

        <link rel="apple-touch-icon" sizes="57x57" href="<%=request.getContextPath()%>/static/img/apple-touch-icon-57x57.png">
        <link rel="apple-touch-icon" sizes="60x60" href="<%=request.getContextPath()%>/static/img/apple-touch-icon-60x60.png">
        <link rel="apple-touch-icon" sizes="72x72" href="<%=request.getContextPath()%>/static/img/apple-touch-icon-72x72.png">
        <link rel="apple-touch-icon" sizes="76x76" href="<%=request.getContextPath()%>/static/img/apple-touch-icon-76x76.png">
        <link rel="apple-touch-icon" sizes="114x114" href="<%=request.getContextPath()%>/static/img/apple-touch-icon-114x114.png">
        <link rel="apple-touch-icon" sizes="120x120" href="<%=request.getContextPath()%>/static/img/apple-touch-icon-120x120.png">
        <link rel="apple-touch-icon" sizes="144x144" href="<%=request.getContextPath()%>/static/img/apple-touch-icon-144x144.png">
        <link rel="apple-touch-icon" sizes="152x152" href="<%=request.getContextPath()%>/static/img/apple-touch-icon-152x152.png">
        <link rel="apple-touch-icon" sizes="180x180" href="<%=request.getContextPath()%>/static/img/apple-touch-icon-180x180.png">
        <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/static/img/favicon-32x32.png" sizes="32x32">
        <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/static/img/android-chrome-192x192.png" sizes="192x192">
        <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/static/img/favicon-96x96.png" sizes="96x96">
        <link rel="icon" type="image/png" href="<%=request.getContextPath()%>/static/img/favicon-16x16.png" sizes="16x16">
        <link rel="manifest" href="<%=request.getContextPath()%>/manifest.json">
        <meta name="msapplication-TileColor" content="#da532c">
        <meta name="msapplication-TileImage" content="<%=request.getContextPath()%>/static/img/mstile-144x144.png">
        <meta name="theme-color" content="#ffffff">

    </head>

    <body>

        <div class="bodywrap">
            <div class="header">
                <div class="container">
                    <a href="<%=request.getContextPath()%>">
                        <div class="dashboard-logo"><img class="logo" alt="Logo - Mobile Technology for Community Health" src="<%=request.getContextPath()%>/static/img/motech-logo.gif"></div>
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
                        <a class="btn btn-default" href="<%=request.getContextPath()%>/module/server/j_spring_security_logout"><span class="fa fa-power-off"></span>&nbsp;<spring:message code="server.signOut"/></a>
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