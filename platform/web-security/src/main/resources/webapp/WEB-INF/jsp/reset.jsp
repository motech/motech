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

        <link rel="stylesheet" type="text/css" href="../../server/resources/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="../../server/resources/css/index.css" />

        <script src="../../server/resources/lib/jquery/jquery.js"></script>
        <script src="../../server/resources/lib/jquery/jquery.migrate.min.js"></script>
        <script src="../../server/resources/lib/bootstrap/bootstrap.min.js"></script>
    </head>

    <body class="body-down">
        <div class="bodywrap">
            <div class="header">
                <div class="container">
                    <a href="."><div class="dashboard-logo hidden-xs" ng-show="showDashboardLogo.showDashboard"><img class="logo" alt="Logo - {{msg('server.motechTitle')}}" src="../server/resources/img/motech-logo.jpg"></div></a>
                    <div class="navbar-collapse">
                        <div class="header-title"><fmt:message key="server.motechTitle" bundle="${bundle}"/></div>
                    </div>
                    <div class="clearfix"></div>
                </div>
            </div>

            <div class="clearfix"></div>
            <div class="navbar-wrapper navbar-default">
                <div class="header-nav navbar">
                    <div class="navbar-inner navbar-inner-bg">
                    </div>
                </div>
            </div>
            <div class="clearfix"></div>
            <div id="content" class="container">
                <div class="row">
                    <div id="main-content">
                        <div class="well2 margin-center margin-before spnw5">
                            <div class="box-header"><fmt:message key="security.resetYourPassword" bundle="${bundle}"/></div>
                            <div class="box-content">
                                <div class="well3">
                                    <c:if test="${not empty errors}">
                                        <div class="login-error">
                                        <c:forEach var="error" items="${errors}">
                                            <fmt:message key="${error}" bundle="${bundle}"/><br/>
                                        </c:forEach>
                                        </div>
                                    </c:if>
                                    <form method="post" action="reset">
                                        <input type="hidden" id="token" name="token" value="${token}" />

                                        <div class="form-group">
                                            <h4><fmt:message key="security.enterNewPassword" bundle="${bundle}"/></h4>
                                        </div>
                                        <div class="form-group">
                                            <label><fmt:message key="security.password" bundle="${bundle}"/></label>
                                            <input class="col-md-12" type="password" id="password" name="password">
                                        </div>
                                        <div class="form-group">
                                            <label><fmt:message key="security.confirmPassword" bundle="${bundle}"/></label>
                                            <input class="col-md-12" type="password" id="passwordConfirmation" name="passwordConfirmation" />
                                        </div>
                                        <div class="form-group">
                                            <input class="btn btn-primary" type="submit" value="<fmt:message key="security.changePassword" bundle="${bundle}"/>"/>
                                            <input class="btn btn-default" type="button" value="<fmt:message key="security.cancel" bundle="${bundle}"/>"/>
                                        </div>
                                    </form>
                                </div>
                                <div class="clearfix"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
