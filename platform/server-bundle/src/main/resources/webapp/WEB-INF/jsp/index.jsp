<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${pageLang}" />
<fmt:setBundle basename="org.motechproject.resources.messages" var="bundle"/>

<!DOCTYPE html>
<html>
<head>
    <%@include file="head.jsp" %>

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
    <div ng-show="ready" id="content-header" ng-include="'../server/resources/partials/header.html'"></div>

    <div id="content" class="container-fluid" ng-controller="HomeCtrl">
        <div class="row-fluid">
            <motech-modules></motech-modules>

            <div id="main-content" class="span10">
                <c:if test="${! empty currentModule}">
                    <div>
                        <div class="splash" ng-hide="ready">
                            <div class="splash-logo"></div>
                            <div class="clearfix"></div>
                            <div class="splash-loader"><img src="../server/resources/img/loader.gif" alt="loading" /></div>
                            <div class="clearfix"></div>
                            <div class="splash-msg">{{msg('module.loading')}}</div>
                            <div class="clearfix"></div>
                        </div>
                        <c:if test="${criticalNotification != null && criticalNotification != ''}">
                            <div id="criticalNotification" class="alert alert-error">
                                ${criticalNotification}
                            </div>
                        </c:if>
                        <div id="module-content" ng-show="ready">
                            <script type="text/javascript">
                                loadModule('${currentModule.url}', ${currentModule.angularModulesStr});
                            </script>
                        </div>
                    </div>
                </c:if>
            </div>

        </div>
    </div>

    <div ng-show="ready" ng-include="'../server/resources/partials/footer.html'"></div>

</div>
</body>
</html>
