<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <%@include file="head.jsp" %>
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
            <h2 class="title ng-binding">{{msg('server.welcome.startup')}}</h2>
            </div>
        </div>
        <div class="clearfix"></div>
        <div class="startup-form">
            <div class="diver">
                <form action="bootstrap.do" method="POST" class="form-horizontal">
                    <div class="control-group">
                        <label class="control-label">{{msg('server.bootstrap.dbUrl')}}</label>
                        <div class="controls">
                            <input type="text" class="input-xlarge" name="dbUrl" value="${bootstrapConfig.dbUrl}"/>
                        </div>
                    </div>
                    <div class="control-group">
                        <label class="control-label">{{msg('server.bootstrap.dbUsername')}}</label>
                        <div class="controls">
                            <input type="text" class="input-xlarge" name="dbUsername" value="${bootstrapConfig.dbUsername}"/>
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label">{{msg('server.bootstrap.dbPassword')}}</label>
                        <div class="controls">
                            <input type="text" class="input-xlarge" name="dbPassword" value="${bootstrapConfig.dbPassword}"/>
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label">{{msg('server.bootstrap.tenantId')}}</label>
                        <div class="controls">
                            <input type="text" class="input-xlarge" name="tenantId" value="${bootstrapConfig.tenantId}"/>
                        </div>
                    </div>

                    <div class="control-group">
                        <label class="control-label">{{msg('server.bootstrap.configSource')}}</label>
                        <div class="controls">
                            <input type="radio" value="file" name="configSource" <c:if test="${bootstrapConfig.configSource == 'file'}">checked=checked</c:if> />
                            <span class="label-radio">{{msg('server.configSource.file')}}</span>
                            <input type="radio" value="ui" name="configSource" <c:if test="${bootstrapConfig.configSource != 'file'}">checked=checked</c:if> />
                            <span class="label-radio">{{msg('server.configSource.ui')}}</span>
                        </div>
                    </div>
                    <div class="control-group">
                        <div class="controls">
                            <input class="btn btn-primary" type="submit" name="BOOTSTRAP" value="{{msg('server.bootstrap.submit')}}"/>
                        </div>
                    </div>
                    <c:if test="${not empty errors}">
                        <div class="alert alert-error">
                            <c:forEach var="error" items="${errors}">
                                {{msg('${error}')}}   <br/>
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
