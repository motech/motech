var widgetModule = angular.module('motech-widgets', []);

widgetModule.directive('bsPopover', function() {
    return function(scope, element, attrs) {
        $(element).popover();
    }
});

widgetModule.directive('taskPopover', function() {
    return function(scope, element, attrs) {
        $(element).popover({
            placement: 'left',
            trigger: 'hover',
            html: true,
            content: function() {
                return $(element).find('.content-task').html();
            }
        });
    }
});