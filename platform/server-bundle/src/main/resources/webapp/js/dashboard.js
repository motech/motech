/*function initAngular() {
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


    // Declare app level module which depends on filters, and services
    App.config(function($stateProvider, $locationProvider, $urlRouterProvider, $ocLazyLoadProvider) {
        $urlRouterProvider.otherwise("/");
        //$locationProvider.hashPrefix('!');

        // You can also load via resolve
        $stateProvider
          .state('index', {
            url: "/", // root route
            views: {
              "lazyLoadView": {
                controller: 'MotechHomeCtrl', // This view will use AppCtrl loaded below in the resolve
                template: "<h1>Home! hello <a ui-sref='email'>email</a></h1> "
              }
            },
            resolve: { // Any property in resolve should return a promise and is executed before the view is loaded
              loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad) {
                // you can lazy load files for an existing module
                return $ocLazyLoad.load('resources/js/controllers.js');
              }]
            }
          })
          .state('email', {
              url: "/email/send", // root route
              views: {
                "lazyLoadView": {
                  //controller: 'app', // This view will use AppCtrl loaded below in the resolve
                  template: "<h1>email <a ui-sref='home'>home</a></h1><h1>email <a ui-sref='index'>index</a></h1>"    //../email/resources/partials/sendEmail.html
                }
              }
            })
            .state('home', {
              url: "/home", // root route
              views: {
                "lazyLoadView": {
                  controller: 'MotechHomeCtrl', // This view will use AppCtrl loaded below in the resolve
                  template: "<h1>Home<a ui-sref='email'>email</a></h1><h1>email <a ui-sref='index'>index</a></h1>"    //../email/resources/partials/sendEmail.html
                }
              }
            })
          ;

        // Without server side support html5 must be disabled.
        $locationProvider.html5Mode(false);

       });

      data[0]
      Object {name: "tasks.controllers", script: "../tasks/js/controllers.js", template: null, css: null}
      data[1]
      Object {name: "tasks.utils", script: "../tasks/js/util.js", template: null, css: null}
      data[2]
      Object {name: "tasks.directives", script: "../tasks/js/directives.js", template: null, css: null}
      data[3]
      Object {name: "tasks.services", script: "../tasks/js/services.js", template: null, css: null}
      data[4]
      Object {name: "tasks.filters", script: "../tasks/js/filters.js", template: null, css: null}
      data[5]
      Object {name: "tasks", script: "../tasks/js/app.js", template: "../tasks/index.html", css: "../tasks/css/tasks.css"}
      data[6]
      Object {name: "email.services", script: "../email/resources/js/services.js", template: null, css: null}
      data[7]
      Object {name: "email.directives", script: "../email/resources/js/directives.js", template: null, css: null}
      data[8]
      Object {name: "email.controllers", script: "../email/resources/js/controllers.js", template: null, css: null}
      data[9]
      Object {name: "email", script: "../email/resources/js/app.js", template: "../email/resources/index.html", css: "../email/resources/css/email.css"}
      data[10]
      Object {name: "data-services.utils", script: "../mds/resources/js/util.js", template: null, css: null}

*/




