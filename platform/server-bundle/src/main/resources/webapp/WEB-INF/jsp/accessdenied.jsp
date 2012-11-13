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

    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap-responsive.css">
    <link rel="stylesheet" type="text/css" href="resources/css/index.css" />

    <script src="resources/lib/jquery/jquery-1.8.2.min.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.form.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery-ui.min.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.alerts.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.i18n.properties-min-1.0.9.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.tools.min.js" type="text/javascript"></script>
    <script src="resources/lib/jquery/jquery.blockUI.js" type="text/javascript"></script>

    <script src="resources/lib/angular/angular.min.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-resource.min.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-cookies.min.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-bootstrap.js" type="text/javascript"></script>
    <script src="resources/lib/angular/angular-ui.min.js" type="text/javascript"></script>

    <script src="resources/lib/bootstrap/bootstrap.min.js"></script>

    <script src="resources/js/util.js" type="text/javascript"></script>
    <script src="resources/js/common.js" type="text/javascript"></script>
    <script src="resources/js/localization.js"></script>
    <script src="resources/js/app.js"></script>
    <script src="resources/js/controllers.js"></script>

    <script src="resources/js/dashboard.js"></script>

    <c:if test="${! empty currentModule}">
       ${currentModule.header}
    </c:if>

    <c:if test="${empty currentModule}">
        <script type="text/javascript">
            $(window).load(function() {
                initAngular();
            });
        </script>
    </c:if>
</head>

<body ng-controller="MasterCtrl"  ng-class="showDashboardLogo.backgroudUpDown()">

<div class="bodywrap">
    <div class="header">
        <div class="container-fluid">
            <div class="dashboard-logo" ng-show="showDashboardLogo.showDashboard"></div>
            <div class="header-title" ng-show="showDashboardLogo.showDashboard"><fmt:message key="motechTitle" bundle="${bundle}"/></div>
        </div>
    </div>
    <div class="clearfix"></div>

    <div class="header-nav navbar">
        <div class="navbar-inner navbar-inverse navbar-inner-bg">
            <div class="container-fluid">
                <a class="btn btn-navbar btn-blue" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>
                <a id="brand" class="brand" ng-hide="showDashboardLogo.showDashboard" href="#">MOTECH</a>

                <div class="nav-collapse">
                    <ul class="nav" role="navigation">
                        <li class="divider-vertical" ng-hide="showDashboardLogo.showDashboard" ></li>
                        <li class="current"><a  role="menu"  href="."><fmt:message key="home" bundle="${bundle}"/></a></li>
                        <li class="divider-vertical divider-vertical-sub"></li>
                        <li><a role="menu"><fmt:message key="motech" bundle="${bundle}"/> <fmt:message key="project" bundle="${bundle}"/></a></li>
                        <li class="divider-vertical divider-vertical-sub"></li>
                        <li><a role="menu"><fmt:message key="community" bundle="${bundle}"/></a></li>
                    </ul>
                    <a id="minimize" class="btn btn-mini btn-blue" ng-click="minimizeHeader()">
                        <img src="resources/img/trans.gif" title="{{msg(showDashboardLogo.changeTitle())}}"
                        alt="{{msg(showDashboardLogo.changeTitle())}}"
                        ng-class="showDashboardLogo.changeClass()"/>
                    </a>
                    <ul class="nav pull-right menu-left">
                        <c:if test="${securityLaunch}">
                        <li class="dropdown">
                            <a class="dropdown-toggle" href="#" data-toggle="dropdown">Logged in as <strong>${userName}</strong><strong class="caret"></strong></a>
                            <ul id="localization" class="dropdown-menu" role="menu">
                                <li><a href="#" tabindex="-1"><i class="icon-user"></i> Profile</a></li>
                                <li class="divider"></li>
                                <li class="dropdown-submenu pull-left">
                                    <a class="menu-flag dropdown-toggle" tabindex="-1" data-toggle="dropdown" href="#">
                                        <i class="flag flag-${pageLang.language}" title="${pageLang.language}" alt="${pageLang.language}"></i>
                                        <span class="text-capitalize">${pageLang.getDisplayLanguage(pageLang)}</span>
                                    </a>
                                    <ul class="dropdown-menu">
                                        <li ng-repeat="(key, value) in languages">
                                            <a ng-click="setUserLang(key)"><i class="flag flag-{{key}}"></i> {{value}}</a>
                                        </li>
                                    </ul>
                                </li>
                                <li class="divider"></li>
                                <li><a href="${contextPath}/j_spring_security_logout" class=""><i class="icon-off"></i> Sign Out</a></li>
                            </ul>
                        </li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <div class="clearfix"></div>

    <div id="content" class="container-fluid">
        <div class="row-fluid">
            <div id="main-content" class="span10">
                    <div>
                        <div class="splash splash-alert">
                            <div class="splash-logo"></div>
                            <div class="clearfix"></div>
                            <div class="splash-loader splash-header-alert">{{msg('accessDenied')}}</div>
                            <div class="clearfix"></div>
                            <div class="splash-msg splash-msg-alert">{{msg('accessDeniedAlert')}}</div>
                            <div class="clearfix"></div>
                        </div>
                    </div>
            </div>

        </div>
    </div>

</div>

<footer class="inside">
<strong><fmt:message key="generatedAt" bundle="${bundle}"/>&#58; </strong> <%= new java.util.Date() %>&#59;
<strong> <fmt:message key="server.time" bundle="${bundle}"/>&#58; </strong>${uptime}
</footer>
</body>
</html>
