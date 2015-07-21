<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html ng-app="motech-dashboard">
<head>
    <meta charset="UTF-8">
    <title>MOTECH - Mobile Technology for Community Health</title>

    <link rel="apple-touch-icon" sizes="57x57" href="./../../static/img/apple-touch-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="60x60" href="./../../static/img/apple-touch-icon-60x60.png">
    <link rel="apple-touch-icon" sizes="72x72" href="./../../static/img/apple-touch-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="76x76" href="./../../static/img/apple-touch-icon-76x76.png">
    <link rel="apple-touch-icon" sizes="114x114" href="./../../static/img/apple-touch-icon-114x114.png">
    <link rel="apple-touch-icon" sizes="120x120" href="./../../static/img/apple-touch-icon-120x120.png">
    <link rel="apple-touch-icon" sizes="144x144" href="./../../static/img/apple-touch-icon-144x144.png">
    <link rel="apple-touch-icon" sizes="152x152" href="./../../static/img/apple-touch-icon-152x152.png">
    <link rel="apple-touch-icon" sizes="180x180" href="./../../static/img/apple-touch-icon-180x180.png">
    <link rel="icon" type="image/png" href="./../../static/img/favicon-32x32.png" sizes="32x32">
    <link rel="icon" type="image/png" href="./../../static/img/android-chrome-192x192.png" sizes="192x192">
    <link rel="icon" type="image/png" href="./../../static/img/favicon-96x96.png" sizes="96x96">
    <link rel="icon" type="image/png" href="./../../static/img/favicon-16x16.png" sizes="16x16">
    <link rel="manifest" href="./../../manifest.json">
    <meta name="msapplication-TileColor" content="#da532c">
    <meta name="msapplication-TileImage" content="./../../static/img/mstile-144x144.png">
    <meta name="theme-color" content="#ffffff">
    <link rel="shortcut icon" href="./../../favicon.ico" type="image/x-icon">

    ${mainHeader}
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
                            <img src="./../../static/img/motech-logo.gif" alt="motech-logo">
                        </div>
                        <div class="clearfix"></div>
                        <div class="splash-loader">
                            <img src="./../../static/img/loadingbar.gif" alt="loading">
                        </div>
                        <div class="clearfix"></div>
                        <div class="splash-msg"></div>
                        <div class="clearfix"></div>
                    </div>
                    <div id="module-content" load-on-demand="moduleToLoad"></div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div> <!-- #outer-center-->

    </div>
</body>
</html>
