(function () {
    'use strict';

    var emailModule = angular.module('motech-email');

    emailModule.directive('richTextEditor', function() {
        return {
            restrict : "A",
            require : 'ngModel',
            link : function(scope, element, attrs, ctrl) {
                var textarea = element.find('.textarea');

                textarea.livequery(function() {
                    var editor;

                    $(this).wysihtml5({
                        "image": false,
                        "color": false,
                        "link": false,
                        events: {
                            change: function() {
                                scope.$apply(function() {
                                    ctrl.$setViewValue(editor.getValue());
                                });
                            }
                        }
                    });

                    editor = $(this).data('wysihtml5').editor;

                    // model -> view
                    ctrl.$render = function() {
                        textarea.html(ctrl.$viewValue);
                        editor.setValue(ctrl.$viewValue);
                    };

                    // load init value from DOM
                    ctrl.$render();
                });
            }
        };
    }).directive('purgeTime', function(){
        return {
            require: 'ngModel',
            link: function(scope, element, attrs, modelCtrl) {
                modelCtrl.$parsers.push(function (inputValue) {
                    if (inputValue === undefined) {
                        return '';
                    }

                    var transformedInput = inputValue.toLowerCase().replace(/[a-z]+$/, '');

                    if (transformedInput !== inputValue) {
                        modelCtrl.$setViewValue(transformedInput);
                        modelCtrl.$render();
                    }

                    return transformedInput;
                });
            }
        };
    });

}());
