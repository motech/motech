<%@ page language="java" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <title>MOTECH - Mobile Technology for Community Health</title>

    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="resources/css/bootstrap-responsive.css">
    <link rel="stylesheet" type="text/css" href="resources/css/index.css" />

    <script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>
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

<div class="bodywrap">
    <div class="header">
        <div class="container">
            <div class="dashboard-logo"></div>
            <div class="header-title">{{msg('motechTitle')}}</div>
            <div class="top-menu">
                <div class="navbar">
                    <ul class="nav">
                        <li><strong>Server up time: </strong>${uptime}</li>
                        <li>|</li>
                        <li><a href=""><strong>{{msg('login')}} </strong></a></li>
                        <li>|</li>
                            <li class="dropdown" id="localization">
                                <a class="menu-flag dropdown-toggle" data-toggle="dropdown" href="#">
                                    <i class="flag flag-{{userLang.key}}" title="{{userLang.key}}" alt="{{userLang.key}}"></i>
                                    {{userLang.value}}
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
            <ul class="nav" role="navigation">
                <li class="current"><a  role="menu"  href=".">{{msg('home')}}</a></li>
                <li><a>|</a></li>
                <li><a role="menu">{{msg('motech')}} {{msg('project')}}</a></li>
                <li><a>|</a></li>
                <li><a role="menu">{{msg('community')}}</a></li>
            </ul>
        </div>
    </div>
    </div>
    <div class="clearfix"></div>

    <div id="content" class="container-fluid">
        <div class="row-fluid">

            <div id="side-nav" class="span2">
                <ul class="nav nav-tabs nav-stacked">
                    <c:forEach var="module" items="${individuals}">
                        <li class="nav-header">{{msg('${module.moduleName}')}}</li>
                        <c:forEach var="entry" items="${module.subMenu}">
                            <li ng-class="active('?moduleName=${module.moduleName}${entry.value}')"><a href="?moduleName=${module.moduleName}${entry.value}">{{msg('${entry.key}')}}</a></li>
                        </c:forEach>
                        <li class="divider"></li>
                    </c:forEach>

                    <c:if test="${not empty links}">
                        <li class="nav-header">{{msg('modules')}}</li>
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
                                loadModule('${currentModule.url}', ${currentModule.angularModulesStr});
                            </script>
                        </div>
                    </div>
                </c:if>
            </div>

        </div>
    </div>

</div>

<footer class="inside"><strong>{{msg('generatedAt')}}:</strong> <%= new java.util.Date() %></footer>
</body>
</html>
