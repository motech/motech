(function () {
    'use strict';

    /* Directives */

    var directives = angular.module('admin.directives', []);

    directives.directive('sidebar', function () {
       return function (scope, element, attrs) {
           $(element).sidebar({
               position:"right"
           });
       };
    });

    directives.directive('clearForm', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                $(element).on('hidden', function () {
                    $('#' + attrs.clearForm).clearForm();
                });
            }
        };
    });

    directives.directive('setLevelFilter', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var elm = angular.element(element), attribute = attrs;
                elm.click(function (e) {
                    if (elm.children().hasClass("fa-check-square-o")) {
                        $(this).children().removeClass('fa-check-square-o').addClass('fa-square-o');
                        $(this).removeClass('active');
                    }
                    else {
                        elm.children().addClass('fa-check-square-o').removeClass('fa-square-o');
                        elm.addClass('active');
                    }
                });
            }
        };
    });

    directives.directive('filereader', function () {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.bind('change', function(e){
                   scope.$apply(function(){
                       scope[attrs.filereader](e.target.files[0]);
                   });
                });
            }
        };
    });

    // TODO: Can be removed after common file upload directive work again. (MOTECH-2265)
    directives.directive('motechFileUploadAdmin', function($http, $templateCache, $compile) {
        return function(scope, element, attrs) {
            $http.get('../server/resources/partials/motech-file-upload.html', { cache: $templateCache }).success(function(response) {
                var contents = element.html(response).contents();
                element.replaceWith($compile(contents)(scope));
            });
        };
    });
}());