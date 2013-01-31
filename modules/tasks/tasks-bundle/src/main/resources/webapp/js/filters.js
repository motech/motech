var widgetModule = angular.module('motech-tasks');

widgetModule.filter('filterPagination', function () {
    return function (input, start) {
        start = +start;
        return input.slice(start);
    }
})

widgetModule.filter('fromNow', function () {
    return function(date) {
        return moment(date).fromNow();
    };
})
