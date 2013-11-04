<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setLocale value="${pageLang}" />
<fmt:setBundle basename="org.motechproject.resources.messages" var="bundle"/>

<!DOCTYPE html>
<html>
<head>
    <%@include file="head.jsp" %>
    <script src="../server/resources/js/startup.js" type="text/javascript"></script>

    <script type="text/javascript">
        $(window).load(function() {
            initAngular();
        });
    </script>

</head>
<body ng-controller="MasterCtrl" class="body-startup">
<div class="bodywrap">
    <div class="nav-collapse">
        <div class="margin-before5"></div>
    </div>
    <div class="clearfix"></div>
    <div class="startup" ng-show="ready">
        <a href="."><div class="startup-logo"><img src="../server/resources/img/motech-logo.jpg" alt="motech-logo" /></div></a>
        <div class="startup-title ng-binding">Mobile Technology for Community Health</div>
        <div class="clearfix"></div>
        <div class="startup-strip">
            <div class="control-group">
            <h2 class="title ng-binding"><fmt:message key="server.welcome.startup" bundle="${bundle}"/></h2>
            </div>
        </div>
        <div class="clearfix"></div>
        <div class="startup-form">
            <div class="diver">
                <form action="startup.do" method="POST" class="form-horizontal">
                    <div ng-show="!${isFileMode}" class="control-group">
                        <label class="control-label"><fmt:message key="server.select.language" bundle="${bundle}"/></label>
                        <div class="controls">
                            <div class="btn-group">
                                <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                                    <i class="flag flag-${startupSettings.language} label-flag"></i> ${languages[startupSettings.language]}
                                    <span class="caret"></span>
                                </a>
                                <ul class="dropdown-menu">
                                    <c:forEach var="entry" items="${languages}">
                                        <li>
                                            <a ng-click="setUserLang('${entry.key}', true)">
                                                <i class="flag flag-${entry.key} label-flag"></i> ${entry.value}
                                            </a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div ng-show="!${isFileMode}" class="control-group">
                        <label class="control-label"><fmt:message key="server.enter.queueUrl" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="text" class="input-xlarge" name="queueUrl" value="${startupSettings.queueUrl}"/>
                            <c:if test="${ not empty suggestions.queueUrls }">
                                <div id="queue.urls" class="queue-urls">
                                <c:forEach var="url" items="${suggestions.queueUrls}" varStatus="status">
                                    <div id="queue.url.${status.count}">
                                        <span><fmt:message key="server.suggestion" bundle="${bundle}"/> #${status.count}: ${url}</span>
                                        <button type="button" class="btn btn-mini"><fmt:message key="server.use" bundle="${bundle}"/></button>
                                    </div>
                                </c:forEach>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div ng-show="!${isFileMode}" class="control-group">
                         <label class="control-label"><fmt:message key="server.select.loginMode" bundle="${bundle}"/></label>
                         <div class="controls">
                             <input type="radio" value="repository" name="loginMode" ng-click="securityMode = 'repository'" ng-checked="securityMode == 'repository'"><span class="label-radio">{{msg('server.repository')}}</span>
                             <input type="radio" value="openId" name="loginMode" ng-click="securityMode = 'openid'" ng-checked="securityMode == 'openid'"/><span class="label-radio">{{msg('server.openId')}}</span>
                         </div>
                     </div>
                    <div ng-show="securityMode=='repository' || ${isFileMode}" class="control-group">
                        <label class="control-label"><fmt:message key="server.enter.adminLogin" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="text" class="input-xlarge" name="adminLogin" value="${startupSettings.adminLogin}"/>
                        </div>
                    </div>
                    <div ng-show="securityMode=='repository' || ${isFileMode}" class="control-group">
                        <label class="control-label"><fmt:message key="server.enter.adminPassword" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="password" class="input-xlarge" name="adminPassword" value="${startupSettings.adminPassword}"/>
                        </div>
                    </div>
                    <div ng-show="securityMode=='repository' || ${isFileMode}" class="control-group">
                        <label class="control-label"><fmt:message key="server.enter.adminComfirmPassword" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="password" class="input-xlarge" name="adminConfirmPassword" value="${startupSettings.adminConfirmPassword}"/>
                        </div>
                    </div>
                     <div ng-show="securityMode=='repository' || ${isFileMode}" class="control-group">
                        <label class="control-label"><fmt:message key="server.enter.adminEmail" bundle="${bundle}"/></label>
                        <div class="controls">
                            <input type="email" class="input-xlarge" name="adminEmail" value="${startupSettings.adminEmail}"/>
                        </div>
                     </div>
                     <div ng-show="securityMode=='openid'" class="control-group">
                         <label class="control-label"><fmt:message key="server.enter.providerName" bundle="${bundle}"/></label>
                         <div class="controls">
                             <input type="text" class="input-xlarge" name="providerName" value="${startupSettings.providerName}"/>
                         </div>
                     </div>
                     <div ng-show="securityMode=='openid'" class="control-group">
                          <label class="control-label"><fmt:message key="server.enter.providerUrl" bundle="${bundle}"/></label>
                          <div class="controls">
                              <input type="text" class="input-xlarge" name="providerUrl" value="${startupSettings.providerUrl}"/>
                          </div>
                     </div>
                    <div class="control-group">
                        <div class="controls">
                            <input type="hidden" name="language" value="${startupSettings.language}"/>
                            <input class="btn btn-primary" type="submit" name="START" value="<fmt:message key="server.submit" bundle="${bundle}"/>"/>
                        </div>
                    </div>
                    <c:if test="${not empty errors}">
                        <div class="alert alert-error">
                        <c:forEach var="error" items="${errors}">
                            <fmt:message key="${error}" bundle="${bundle}"/><br/>
                        </c:forEach>
                        </div>
                    </c:if>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
