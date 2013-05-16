function loadModule(url, angularModules) {
    $('#module-content').load(url, function() {
        if (angularModules) {
            angular.bootstrap(document, angularModules);
        } else {
            initAngular();
        }
    });
}

function initAngular() {
    angular.bootstrap(document, ["motech-dashboard"]);
}




