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

    <div class="clearfix"></div>
    <div id="content" class="container">
        <div class="row-fluid">
            <div id="main-content">
                <div class="well2 margin-center spnw5">
                <div class="box-header"><fmt:message key="security.forgotPassword" bundle="${bundle}"/></div>

                <div class="box-content well3">
                    <c:choose>
                        <c:when test="${error == null}">
                            <div class="control-group">
                                <p><fmt:message key="security.tokenSent" bundle="${bundle}"/></p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="login-error">
                                <h4><fmt:message key="security.tokenSendError" bundle="${bundle}"/></h4>
                            </div>
                            <div class="control-group login-error">
                                <p><fmt:message key="${error}" bundle="${bundle}"/></p>
                            </div>
                            <div class="control-group login-error">      <a ></a>
                                <a href="."><input type="button" class="btn btn-primary" value="<fmt:message key="security.back" bundle="${bundle}"/>" /></a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="clearfix"></div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

