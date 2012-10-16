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

    <script src="resources/lib/jquery/jquery-1.8.2.min.js" type="text/javascript">></script>
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

    <script src="resources/lib/bootstrap/bootstrap-modal.js"></script>
    <script src="resources/lib/bootstrap/bootstrap-tabs.js"></script>

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

<body ng-controller="MasterCtrl">

<div ng-class="showDashboardLogo.backgroudUpDown()">
    <div class="header">
        <div class="container">
            <div class="dashboard-logo" ng-show="showDashboardLogo.showDashboard"></div>
            <div class="header-title" ng-show="showDashboardLogo.showDashboard"><fmt:message key="motechTitle" bundle="${bundle}"/></div>
            <div class="top-menu">
                <div class="navbar">
                    <ul class="nav">
                        <li><strong><fmt:message key="server.time" bundle="${bundle}"/>: </strong>${uptime}</li>
                        <li>|</li>
                        <li><a href=""><strong><fmt:message key="login" bundle="${bundle}"/> </strong></a></li>
                        <li>|</li>
                            <li class="dropdown" id="localization">
                                <a class="menu-flag dropdown-toggle" data-toggle="dropdown" href="#">
                                    <i class="flag flag-${pageLang.language}" title="${pageLang.language}" alt="${pageLang.language}"></i>
                                    <span style="text-transform:capitalize;">${pageLang.getDisplayLanguage(pageLang)}</span>
                                    <span class="caret"></span>
                                </a>
                                <ul class="dropdown-menu">
                                    <li ng-repeat="(key, value) in languages">
                                        <a ng-click="setUserLang(key)"><i class="flag flag-{{key}}"></i> {{value}}</a>
                                    </li>
                                </ul>
                            </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    <div class="clearfix"></div>

    <div class="header-nav navbar">
        <div class="navbar-inner navbar-inner-bg">

            <a id="brand" class="brand" ng-hide="showDashboardLogo.showDashboard" href="#">MOTECH</a>
            <ul class="nav" role="navigation">
                <li class="divider-vertical" ng-hide="showDashboardLogo.showDashboard" ></li>
                <li class="current"><a  role="menu"  href="."><fmt:message key="home" bundle="${bundle}"/></a></li>
                <li><a>|</a></li>
                <li><a role="menu"><fmt:message key="motech" bundle="${bundle}"/> <fmt:message key="project" bundle="${bundle}"/></a></li>
                <li><a>|</a></li>
                <li><a role="menu"><fmt:message key="community" bundle="${bundle}"/></a></li>
            </ul>
            <a id="minimize" ng-click="minimizeHeader()">
                                <img src="resources/img/trans.gif" title="{{msg(showDashboardLogo.changeTitle())}}" alt="{{msg(showDashboardLogo.changeTitle())}}"
                                    ng-class="showDashboardLogo.changeClass()"/>
            </a>
        </div>
    </div>

    <div class="clearfix"></div>

    <div id="content" class="container-fluid">
        <div class="row-fluid">

            <div id="side-nav" class="span2">
                <ul class="nav nav-tabs nav-stacked">
                    <c:forEach var="module" items="${individuals}">
                        <li class="nav-header"><fmt:message key="${module.moduleName}" bundle="${bundle}"/></li>
                        <c:forEach var="entry" items="${module.subMenu}">
                            <li ng-class="active('?moduleName=${module.moduleName}${entry.value}')"><a href="?moduleName=${module.moduleName}${entry.value}"><fmt:message key="${entry.key}" bundle="${bundle}"/></a></li>
                        </c:forEach>
                        <li class="divider"></li>
                    </c:forEach>

                    <c:if test="${not empty links}">
                        <li class="nav-header"><fmt:message key="modules" bundle="${bundle}"/></li>
                        <c:forEach var="module" items="${links}">
                            <li <c:if test="${module.moduleName == currentModule.moduleName}">class='active'</c:if>><a href="?moduleName=${module.moduleName}">${module.moduleName}</a></li>
                        </c:forEach>
                    </c:if>
                </ul>
            </div>

            <div id="main-content" class="span10">
                <c:if test="${! empty currentModule}">
                    <div>
                        <div id="module-content">
                            <script type="text/javascript">
                                $.blockUI({
                                    message : '<h3><img src="resources/img/bigloader.gif" alt="loading" /></h3>'
                                });
                                loadModule('${currentModule.url}', ${currentModule.angularModulesStr});
                                $.unblockUI();
                            </script>
                        </div>
                    </div>
                </c:if>
            </div>

        </div>
    </div>

</div>

<footer class="inside"><strong><fmt:message key="generatedAt" bundle="${bundle}"/>:</strong> <%= new java.util.Date() %></footer>
</body>
</html>
