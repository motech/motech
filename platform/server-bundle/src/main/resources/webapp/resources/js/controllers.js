'use strict';

function MasterCtrl($scope, $http, i18nService) {
    var msgBundle = 'dashboard';
    var msgPath = 'resources/messages/';

    $scope.languages = [];
    $scope.userLang = null;

    $http({method: 'GET', url: 'lang/list'}).
        success(function(data) {
            $scope.languages = data;
        });

    $http({method: 'GET', url: 'lang'}).
        success(function(data) {
            i18nService.init(data, msgBundle, msgPath);
            i18nService.init(data, 'startup', msgPath);
            i18nService.init(data, 'messages', '../demo/messages/');
            $scope.userLang = $scope.getLanguage(toLocale(data));
        });

    $scope.setUserLang = function(lang) {
        var locale = toLocale(lang);

        $http({ method: "POST", url: "lang", params: locale }).success(function() {
            i18nService.init(lang, msgBundle, msgPath);
            i18nService.init(lang, 'startup', msgPath);
            i18nService.init(lang, 'messages', '../demo/messages/');
            $scope.userLang = $scope.getLanguage(locale);
        });
    }

    $scope.msg = function(key) {
        return i18nService.getMessage(key);
    };

    $scope.setModuleUrl = function(url) {
        $scope.moduleUrl = url;
    }

    $scope.getLanguage = function(locale) {
       return {
           key: locale.toString() || "en",
           value: $scope.languages[locale.toString()] || $scope.languages[locale.withoutVariant()] || $scope.languages[locale.language] || "English"
       }
    }
}