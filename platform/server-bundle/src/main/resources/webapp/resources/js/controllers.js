'use strict';

function MasterCtrl($scope, $http, i18nService) {
    var msgBundle = 'dashboard';
    var msgPath = 'resources/messages/';

    $scope.languages = i18nService.languages;
    $scope.userLang = null;

    $http({method: 'GET', url: 'lang'}).
        success(function(data) {
            i18nService.init(data, msgBundle, msgPath);
            i18nService.init(data, 'messages', '../demo/messages/');
            $scope.userLang = i18nService.getLanguage(toLocale(data));
        });

    $scope.setUserLang = function(lang) {
        var locale = toLocale(lang);

        $http({ method: "POST", url: "lang", params: locale }).success(function() {
            i18nService.init(lang, msgBundle, msgPath);
            $scope.userLang = i18nService.getLanguage(locale);
        });
    }

    $scope.msg = function(key) {
        return i18nService.getMessage(key);
    };

    $scope.setModuleUrl = function(url) {
        $scope.moduleUrl = url;
    }
}