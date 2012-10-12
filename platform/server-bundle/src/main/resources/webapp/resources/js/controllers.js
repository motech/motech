'use strict';

function MasterCtrl($scope, $http, i18nService) {
    $scope.i18n = {};
    $scope.languages = [];
    $scope.userLang = null;

    $http({method: 'GET', url: 'lang/locate'}).
        success(function(data) {
            $scope.i18n = data;
        });

    $http({method: 'GET', url: 'lang/list'}).
        success(function(data) {
            $scope.languages = data;
        });

    $http({method: 'GET', url: 'lang'}).
        success(function(data) {
            var key, i;

            for (key in $scope.i18n) {
                for (i = 0; i < $scope.i18n[key].length; i += 1) {
                    i18nService.init(data, key, $scope.i18n[key][i]);
                }
            }
            $scope.userLang = $scope.getLanguage(toLocale(data));
        });

    $scope.setUserLang = function(lang) {
        var locale = toLocale(lang);

        $http({ method: "POST", url: "lang", params: locale }).success(function() {
            var key, i;

            for (key in $scope.i18n) {
                for (i = 0; i < $scope.i18n[key].length; i += 1) {
                    i18nService.init(lang, key, $scope.i18n[key][i]);
                }
            }
            $scope.userLang = $scope.getLanguage(locale);
        });
    }

    $scope.msg = function(key) {
        return i18nService.getMessage(key);
    };

    $scope.setModuleUrl = function(url) {
        $scope.moduleUrl = url;
    }

    $scope.printDate = function(milis) {
        var date = "";
        if (milis) {
            var date = new Date(milis);
        }
        return date;
    }

    $scope.getLanguage = function(locale) {
       return {
           key: locale.toString() || "en",
           value: $scope.languages[locale.toString()] || $scope.languages[locale.withoutVariant()] || $scope.languages[locale.language] || "English"
       }
    }

    $scope.active = function(address) {
        if (window.location.href.indexOf(address) != -1) {
            return "active";
        }
    }

    $scope.minimizeHeader = function() {
        var display = $(".dashboard-logo").css("display");
        if (display == "none") {
            $("body").css("background-position","0 0");
            $(".divider-vertical,#brand").css("display","none");
            $(".header-title,.dashboard-logo").css("display","block");
            $(".minimize").attr("alt", $scope.msg('minimizeLogo'));
            $(".minimize").attr("title", $scope.msg('minimizeLogo'));
            $(".minimize").removeClass("action-minimize-down");
            $(".minimize").addClass("action-minimize-up");
        }
        else {
            $("body").css("background-position","0 -40px");
            $(".divider-vertical,#brand").css("display","block");
            $(".header-title,.dashboard-logo").css("display","none");
            $(".minimize").attr("alt", $scope.msg('expandLogo'));
            $(".minimize").attr("title", $scope.msg('expandLogo'));
            $(".minimize").removeClass("action-minimize-up");
            $(".minimize").addClass("action-minimize-down");
        };

    }
}