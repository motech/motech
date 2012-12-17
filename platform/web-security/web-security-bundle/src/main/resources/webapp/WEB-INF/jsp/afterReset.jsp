<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${pageLang}" />
<fmt:setBundle basename="org.motechproject.resources.messages" var="bundle"/>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>MOTECH - Mobile Technology for Community Health</title>

        <link rel="stylesheet" type="text/css" href="../../server/resources/css/bootstrap.css">
        <link rel="stylesheet" type="text/css" href="../../server/resources/css/bootstrap-responsive.css">
        <link rel="stylesheet" type="text/css" href="../../server/resources/css/index.css" />

        <script src="../server/resources/lib/bootstrap/bootstrap.min.js"></script>
    </head>

    <body class="body-down">
        <div class="bodywrap">
            <div class="header">
                <div class="container">
                    <a href="."><div class="dashboard-logo"></div></a>
                    <div class="nav-collapse">
                        <div class="header-title"><fmt:message key="motechTitle" bundle="${bundle}"/></div>
                    </div>
                    <div class="clearfix"></div>
                </div>
            </div>

            <div class="clearfix"></div>
            <div class="nav-collapse">
                <div class="header-nav navbar">
                    <div class="navbar-inner navbar-inner-bg">
                    </div>
                </div>
            </div>

            <div class="well2 margin-center margin-before2 spnw5">
                <div class="box-header"><fmt:message key="security.resetYourPassword" bundle="${bundle}"/></div>
                <div class="box-content">
                    <div class="well3">
                        <c:choose>
                            <c:when test="${not empty errors}">
                                <div class="login-error">
                                <c:forEach var="error" items="${errors}">
                                    <fmt:message key="${error}" bundle="${bundle}"/><br/>
                                </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div>
                                    <p><fmt:message key="security.resetSuccess" bundle="${bundle}"/></p>
                                    <p><a href="../../.."><fmt:message key="login" bundle="${bundle}"/></a></p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="clearfix"></div>
                </div>
            </div>
        </div>
    </body>
</html>