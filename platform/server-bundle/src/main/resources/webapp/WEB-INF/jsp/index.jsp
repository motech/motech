<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>MOTECH - Mobile Technology for Community Health</title>

    ${mainHeader}

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

<body ng-controller="MasterCtrl" id="container" ng-class="showDashboardLogo.backgroudUpDown()" class="custom ui-layout-container" layout state="bodyState" ng-init="bodyState = true">
    <div ng-controller="HomeCtrl">

        <div class="ui-layout-pane ui-layout-pane-north" id="outer-north">
            <div ng-show="ready" id="content-header" ng-include="'../server/resources/partials/header.html'"></div>
        </div>

        <div id="page-loading">Loading...</div>

        <div ng-show="ready" id="outer-south" class="ui-layout-pane ui-layout-pane-south">
            <span id="tbarCloseSouth" class="southpane-open pull-right" title="Close This Pane"><i class="icon-caret-down button"></i></span>
            <div ng-include="'../server/resources/partials/footer.html'"></div>
        </div>

        <div id="outer-west" class="ui-layout-pane ui-layout-pane-west">
            <div class="header-toolbar header-footer"><i id="tbarCloseWest" class="button icon-caret-left"></i></div>
            <div class="ui-layout-content">
                <motech-modules></motech-modules>
            </div>
        </div>

        <div innerlayout id="outer-center" class="outer-center ui-layout-pane ui-layout-pane-center ui-layout-container">
            <div id="main-content">
                <c:choose>
                    <c:when test="${isAccessDenied}">
                        <div ng-include="'../server/resources/partials/access-denied-splash.html'"></div>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${! empty currentModule}">
                        <div class="splash" ng-hide="ready">
                            <div class="splash-logo"></div>
                            <div class="clearfix"></div>
                            <div class="splash-loader"><img src="../server/resources/img/loader.gif" alt="loading" /></div>
                            <div class="clearfix"></div>
                            <div class="splash-msg">{{msg('server.module.loading')}}</div>
                            <div class="clearfix"></div>
                        </div>
                        <c:if test="${criticalNotification != null && criticalNotification != ''}">
                            <div id="criticalNotification" class="alert alert-danger">
                                ${criticalNotification}
                            </div>
                        </c:if>
                        <div id="module-content" ng-show="ready">
                            <script type="text/javascript">
                                loadModule('${currentModule.url}', ${currentModule.angularModulesStr});
                            </script>
                        </div>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </div> <!-- #outer-center-->

    </div>
</body>
</html>
