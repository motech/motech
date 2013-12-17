<%@ page language="java" pageEncoding="UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${pageLang}"/>
<fmt:setBundle basename="org.motechproject.resources.messages" var="bundle"/>

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
            $(window).load(function () {
                initAngular();
            });
        </script>
    </c:if>
</head>
<body class="body-down" ng-controller="MasterCtrl">
<div class="bodywrap">
    <div class="header">
        <div class="container">
            <a href=".">
                <div class="dashboard-logo hidden-xs" ng-show="showDashboardLogo.showDashboard"><img class="logo" alt="Logo - {{msg('server.motechTitle')}}" src="../server/resources/img/motech-logo.jpg"></div>
            </a>

            <div class="navbar-collapse hidden-xs">
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

    <!-- TODO: Repository and OpenID sections are repeated twice here. Would be better to extract it as a separate jsps and have it in only one place.-->

    <div class="clearfix"></div>
    <div id="content" class="container">
        <div class="row">
            <div id="main-content">
                <c:if test="${empty error}">
                    <div id="login" class="well2 margin-center margin-before spnw55">
                        <div class="box-header"><fmt:message key="security.signInUser" bundle="${bundle}"/></div>
                        <div class="box-content clearfix">
                            <div class="well3">
                                <c:if test="${loginMode.isRepository()}">
                                    <form action="${contextPath}j_spring_security_check" method="POST" class="inside">
                                        <div class="form-group">
                                            <h4><fmt:message key="security.signInWithId"
                                                             bundle="${bundle}"/>&nbsp;<fmt:message
                                                    key="security.motechId"
                                                    bundle="${bundle}"/></h4>
                                        </div>
                                        <div class="form-group margin-before2">
                                            <input element-focus class="col-sm-12 form-control" type="text" name="j_username"
                                                   placeholder="<fmt:message key="security.userName" bundle="${bundle}"/>"/>
                                        </div>
                                        <div class="form-group">
                                            <input class="col-sm-12 form-control" type="password" name="j_password"
                                                   placeholder="<fmt:message key="security.password" bundle="${bundle}"/>"/>
                                        </div>
                                        <div class="form-group">
                                            <input class="btn btn-primary"
                                                   value="<fmt:message key="security.signin" bundle="${bundle}"/>"
                                                   type="submit"/>
                                        <span class="pull-right margin-before05"><a
                                                href="../../module/websecurity/api/forgot"><fmt:message
                                                key="security.signInQuestions" bundle="${bundle}"/></a></span>
                                        </div>
                                    </form>
                                </c:if>
                                <c:if test="${loginMode.isOpenId()}">
                                    <div class="clearfix"></div>
                                    <form class="inside form-horizontal"
                                          action="${contextPath}j_spring_openid_security_check" method="POST">
                                        <div class="form-group open-id">
                                            <p>For ${openIdProviderName} users:&nbsp;&nbsp;</p>
                                            <input name="openid_identifier" type="hidden"
                                                   value="${openIdProviderUrl}"/>
                                            <fmt:message key="security.signInWith" bundle="${bundle}" var="msg"/>
                                            <input class="btn btn-primary" type="submit"
                                                   value="${msg} ${openIdProviderName}"/>
                                        </div>
                                        <div class="form-group open-id">
                                            <p><fmt:message key="server.oneTimeToken" bundle="${bundle}"/>&nbsp;
                                                <a href="../../module/websecurity/api/forgotOpenId"><fmt:message
                                                        key="security.clickHere" bundle="${bundle}"/></a></p>
                                        </div>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:if>
                <c:if test="${error=='true'}">
                    <div class="well2 margin-center margin-before spnw10">
                        <div class="box-header"><fmt:message key="security.signInUnsuccessful"
                                                             bundle="${bundle}"/></div>
                        <div class="box-content clearfix">
                            <div class="row">
                                <div class="col-md-6 inside">
                                    <div class="well3">
                                        <div class="form-group margin-before">
                                            <h4 class="login-error"><fmt:message key="security.wrongPassword"
                                                                                 bundle="${bundle}"/></h4>
                                        </div>
                                        <div class="form-group margin-before2">
                                            <h5 class="login-error"><fmt:message key="security.didnotRecognizeMsg"
                                                                                 bundle="${bundle}"/></h5>
                                        </div>
                                        <div class="form-group margin-before2">
                                            <h5><fmt:message key="security.donotRememberMsg1" bundle="${bundle}"/>
                                                <a href="../../module/websecurity/api/forgot"><fmt:message
                                                        key="security.clickHere" bundle="${bundle}"/></a>
                                                <fmt:message key="security.donotRememberMsg2" bundle="${bundle}"/></h5>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="well3">
                                        <div class="left-divider">
                                            <c:if test="${loginMode.isRepository()}">
                                                <form class="inside" action="${contextPath}j_spring_security_check"
                                                      method="POST">
                                                    <div class="form-group">
                                                        <h4><fmt:message key="security.signInWithId"
                                                                         bundle="${bundle}"/>&nbsp;<fmt:message
                                                                key="security.motechId" bundle="${bundle}"/></h4>
                                                    </div>
                                                    <div class="form-group margin-before2">
                                                        <input element-focus class="col-sm-12 form-control" type="text"
                                                               name="j_username"
                                                               placeholder="<fmt:message key="security.userName" bundle="${bundle}"/>">
                                                    </div>
                                                    <div class="form-group">
                                                        <input class="col-sm-12 form-control" type="password" name="j_password"
                                                               placeholder="<fmt:message key="security.password" bundle="${bundle}"/>">
                                                    </div>
                                                    <div class="form-group">
                                                        <input class="btn btn-primary" type="submit"
                                                               value="<fmt:message key="security.signin" bundle="${bundle}"/>"/>
                                                    </div>
                                                </form>
                                            </c:if>
                                            <c:if test="${loginMode.isOpenId()}">
                                                <form class="inside form-horizontal"
                                                      action="${contextPath}j_spring_openid_security_check"
                                                      method="POST">
                                                    <div class="form-group open-id">
                                                        <p>For ${openIdProviderName} users:&nbsp;&nbsp;</p>
                                                        <input name="openid_identifier" type="hidden"
                                                               value="${openIdProviderUrl}"/>
                                                        <fmt:message key="security.signInWith" bundle="${bundle}"
                                                                     var="msg"/>
                                                        <input class="btn btn-primary" type="submit"
                                                               value="${msg} ${openIdProviderName}"/>
                                                    </div>
                                                    <div class="form-group open-id">
                                                        <p><fmt:message key="server.oneTimeToken"
                                                                        bundle="${bundle}"/>&nbsp;&nbsp;<a
                                                                href="../../module/websecurity/api/forgotOpenId"><fmt:message
                                                                key="security.clickHere" bundle="${bundle}"/></a>
                                                        </p>
                                                    </div>
                                                </form>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>
</body>
</html>
