{
    "lib": [{
        "path": "jquery/jquery.js",
        "order": "first"
    }, {
        "path": "jquery/jquery.migrate.min.js",
        "after": "jquery/jquery.js"
    }, {
        "path": "angular/angular.min.js",
        "after": "jquery/jquery.migrate.min.js"
    }, {
        "path": "jquery/jquery-ui.js",
        "after": "angular/angular.min.js"
    }, {
       "path": "bootstrap/bootstrap-fileupload.min.js",
       "order": "last"
    }],
    "js": [{
        "path": "app.js",
        "before": "controllers.js"
    }],
    "css": [{
        "path": "jquery-ui.min.css",
        "order": "first"
    }, {
        "path": "jquery.ui.theme.css",
        "after": "jquery-ui.min.css"
    }, {
        "path": "bootstrap-theme.min.css",
        "after": "bootstrap.min.css"
    }, {
        "path": "bootstrap-fileupload.min.css",
        "after": "bootstrap-theme.min.css"
    }, {
        "path": "index.css",
        "order": "last"
    }]
}
