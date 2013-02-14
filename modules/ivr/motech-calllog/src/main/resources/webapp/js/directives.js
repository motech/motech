/* put any angular directives here */

angular.module('callLog').directive('tooltip', function() {

    return {
        restrict: 'A',
        link: function(scope, element, attr) {

            var tooltipContainer = $($($(element).parents('div')[0]).children('.tooltip')[0]);

            element.popover({
                trigger: 'click',
                placement: 'bottom',
                html: true,
                title: $(tooltipContainer.children()[0]),
                content: $(tooltipContainer.children()[1]),
            });
        }
    }
});

angular.module('callLog').directive('typeahead', function() {

    return {
        restrict: 'A',
        link: function(scope, element, attr) {
            $.get("../callLog/phone-numbers", function(data) {
                element.typeahead({
                    source: data
                });
            });
        }
    }
});
