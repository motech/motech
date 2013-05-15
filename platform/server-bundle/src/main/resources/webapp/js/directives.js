var widgetModule = angular.module('motech-widgets', []);

widgetModule.directive('bsPopover', function() {
    return function(scope, element, attrs) {
        $(element).popover({
            content: function () {
                return attrs.bsPopover;
            }
        });
    }
});

widgetModule.directive('goToTop', function () {
    return function (scope, element, attrs) {
        $(element).click(function () {
            $('body, html').animate({
                scrollTop: 0
            }, 800);

            return false;
        });
    }
});

widgetModule.directive('goToEnd', function () {
    return function (scope, element, attrs) {
        $(element).click(function () {
            $('body, html').animate({
                scrollTop: $(document).height()
            }, 800);

            return false;
        });
    }
});

widgetModule.directive('ngDateTimePicker', function () {
    return function (scope, element, attributes) {
        $(element).datetimepicker();
    };
});
