<!DOCTYPE html>
<html ng-app="motech-dashboard">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>MOTECH - Mobile Technology for Community Health</title>

    ${mainHeader}
</head>
<body ng-controller="MasterCtrl" class="body-startup">
<div class="bodywrap">
    <div class="navbar-collapse hidden-xs">
        <div class="margin-before5"></div>
    </div>
    <div class="clearfix"></div>
    <div class="startup" ng-show="ready">
        <a href="."><div class="startup-logo"><img src="../server/resources/img/motech-logo.jpg" alt="motech-logo" /></div></a>
        <div class="startup-title ng-binding">Mobile Technology for Community Health</div>
        <div class="clearfix"></div>
        <div class="startup-strip">
            <div class="form-group" ng-show="!requireConfigFiles">
                <h2 class="title ng-binding">{{msg('server.welcome.startup')}}</h2>
            </div>
            <div class="form-group alert-danger" ng-show="requireConfigFiles">
                <h2 class="title ng-binding">{{msg('server.error.config.file.required')}}</h2>
            </div>
        </div>
        <div class="clearfix"></div>
        <div class="startup-form" ng-show="!requireConfigFiles">
            <div class="diver">
                <form id="startup-config-form" ng-init="getStartupViewData()" ng-submit="submitStartupConfig()" method="POST" class="form-horizontal">
                    <div ng-show="!startupViewData.isFileMode" class="form-group">
                        <label class="col-sm-4 control-label">{{msg('server.select.language')}}</label>
                        <div class="col-sm-6">
                            <div class="btn-group">
                                <a class="btn btn-default dropdown-toggle" data-toggle="dropdown" href="#">
                                    <i class="flag flag-{{startupViewData.startupSettings.language}} label-flag"></i> {{startupViewData.languages[startupViewData.startupSettings.language]}}
                                    <span class="caret"></span>
                                </a>
                                <ul class="dropdown-menu">
                                    <li ng-repeat="(key, value) in startupViewData.languages">
                                        <a ng-click="setUserLang(key, true)">
                                            <i class="flag flag-{{key}} label-flag"></i> {{value}}
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div ng-show="!startupViewData.isFileMode" class="form-group">
                        <label class="col-sm-4 control-label">{{msg('server.enter.queueUrl')}}</label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control" name="queueUrl" ng-model="startupViewData.startupSettings.queueUrl"/>
                            <div id="queue.urls" class="queue-urls" ng-repeat="url in startupViewData.suggestions.queueUrls">
                                <div id="queue.url.{{$index}}">
                                    <span>{{msg('server.suggestion')}}#{{$index}}: {{url}}</span>
                                    <button type="button" ng-click="setSuggestedValue(startupViewData.startupSettings, 'queueUrl', url)" class="btn btn-default btn-xs">{{msg('server.use')}}</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div ng-show="!startupViewData.isFileMode" class="form-group">
                         <label class="col-sm-4 control-label">{{msg('server.select.loginMode')}}</label>
                         <div class="col-sm-8">
                             <label class="radio-inline">
                                <input type="radio" value="repository" name="loginMode" ng-click="securityMode = 'repository'" ng-checked="securityMode == 'repository'">
                                {{msg('server.repository')}}
                             </label>
                             <label class="radio-inline">
                                <input type="radio" value="openId" name="loginMode" ng-click="securityMode = 'openid'" ng-checked="securityMode == 'openid'"/>
                                {{msg('server.openId')}}
                             </label>
                         </div>
                     </div>
                    <div ng-show="!startupViewData.isAdminRegistered && (securityMode=='repository' || startupViewData.isFileMode)" class="form-group">
                        <label class="col-sm-4 control-label">{{msg('server.enter.adminLogin')}}</label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control" name="adminLogin" ng-model="startupViewData.startupSettings.adminLogin"/>
                        </div>
                    </div>
                    <div ng-show="!startupViewData.isAdminRegistered && (securityMode=='repository' || startupViewData.isFileMode)" class="form-group">
                        <label class="col-sm-4 control-label">{{msg('server.enter.adminPassword')}}</label>
                        <div class="col-sm-6">
                            <input type="password" class="form-control" name="adminPassword" ng-model="startupViewData.startupSettings.adminPassword"/>
                        </div>
                    </div>
                    <div ng-show="!startupViewData.isAdminRegistered && (securityMode=='repository' || startupViewData.isFileMode)" class="form-group">
                        <label class="col-sm-4 control-label">{{msg('server.enter.adminConfirmPassword')}}</label>
                        <div class="col-sm-6">
                            <input type="password" class="form-control" name="adminConfirmPassword" ng-model="startupViewData.startupSettings.adminConfirmPassword"/>
                        </div>
                    </div>
                     <div ng-show="!startupViewData.isAdminRegistered && (securityMode=='repository' || startupViewData.isFileMode)" class="form-group">
                        <label class="col-sm-4 control-label">{{msg('server.enter.adminEmail')}}</label>
                        <div class="col-sm-6">
                            <input type="email" class="form-control" name="adminEmail" ng-model="startupViewData.startupSettings.adminEmail"/>
                        </div>
                     </div>
                     <div ng-show="securityMode=='openid'" class="form-group">
                         <label class="col-sm-4 control-label">{{msg('server.enter.providerName')}}</label>
                         <div class="col-sm-6">
                             <input type="text" class="form-control" name="providerName" ng-model="startupViewData.startupSettings.providerName"/>
                         </div>
                     </div>
                     <div ng-show="securityMode=='openid'" class="form-group">
                          <label class="col-sm-4 control-label">{{msg('server.enter.providerUrl')}}</label>
                          <div class="col-sm-6">
                              <input type="text" class="form-control" name="providerUrl" ng-model="startupViewData.startupSettings.providerUrl"/>
                          </div>
                     </div>
                    <div class="form-group">
                        <div class="col-sm-offset-4 col-sm-8">
                            <input type="hidden" name="language" ng-model="startupViewData.startupSettings.language"/>
                            <input class="btn btn-primary" type="submit" name="START" value="{{msg('server.submit')}}"/>
                        </div>
                    </div>
                    <div ng-show="errors" class="alert alert-danger">
                        <div ng-repeat="error in errors">
                            {{msg(error)}}<br/>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
