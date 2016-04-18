<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html ng-app="motech-dashboard">
<head>
    <meta charset="UTF-8">
    <title>MOTECH - Mobile Technology for Community Health</title>

    <%@ include file="header.jsp" %>

    <script type="text/javascript" src="resources/js/app.js"></script>
    <script type="text/javascript" src="resources/js/services.js"></script>
    <script type="text/javascript" src="resources/js/controllers.js"></script>
</head>

<body ng-controller="MotechMasterCtrl" id="container" ng-class="showDashboardLogo.backgroundUpDown()" class="custom ui-layout-container" layout state="bodyState" ng-init="bodyState = true">
    <div ng-controller="MotechHomeCtrl">

        <div ng-show="ready" class="ui-layout-pane ui-layout-pane-north" id="outer-north" ng-cloak>
            <div id="content-header" ng-include="'../server/resources/partials/header.html'"></div>
        </div>

        <div ng-show="ready" id="outer-south" class="ui-layout-pane ui-layout-pane-south" ng-cloak>
            <span id="tbarCloseSouth" class="southpane-open pull-right" title="Close This Pane"><i class="fa fa-caret-down button"></i></span>
            <div ng-include="'../server/resources/partials/footer.html'"></div>
        </div>

        <div ng-show="ready" id="outer-west" class="ui-layout-pane ui-layout-pane-west" ng-cloak>
            <div class="header-toolbar header-footer"><i id="tbarCloseWest" class="button fa fa-caret-left"></i></div>
            <div class="ui-layout-content" ng-cloak>
                <motech-modules></motech-modules>
            </div>
        </div>

        <div id="outer-center" class="outer-center ui-layout-pane ui-layout-pane-center ui-layout-container">
            <div id="main-content">
                <c:choose>
                    <c:when test="${isAccessDenied}">
                    <div ng-include="'../server/resources/partials/access-denied-splash.html'"></div>
                    </c:when>
                    <c:otherwise>
                    <div class="splash" ng-hide="ready">
                        <div class="splash-logo">
                            <img src="./../../static/common/img/motech-logo.gif" alt="motech-logo">
                        </div>
                        <div class="clearfix"></div>
                        <div class="splash-loader">
                            <img src="./../../static/common/img/loadingbar.gif" alt="loading">
                        </div>
                        <div class="clearfix"></div>
                        <div class="splash-msg"></div>
                        <div class="clearfix"></div>
                    </div>
                    <div id="module-content" ui-view="moduleToLoad">
                        <!-- default view -->
                        <div ng-include="'../server/resources/partials/main.html'"></div>
                    </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div> <!-- #outer-center-->

    </div>
</body>
</html>
