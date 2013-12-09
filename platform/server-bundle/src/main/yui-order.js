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
    }],
    "js": [{
        "path": "app.js",
        "before": "controllers.js"
    }],
    "css": [{
        "path": "index.css",
        "order": "last"
    }]
}
