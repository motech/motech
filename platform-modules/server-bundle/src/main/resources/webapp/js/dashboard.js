function initAngular() {
    'use strict';
    angular.bootstrap(document, ["motech-dashboard"]);
}

function loadModule(url, angularModules) {
    'use strict';
    $('#module-content').load(url, function() {
        if (angularModules) {
            angular.bootstrap(document, angularModules);
        } else {
            initAngular();
        }
    });
}




