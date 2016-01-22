var config = require('../config');
var gulp = require('gulp');
var path = require('path');

gulp.task('assets', function () {
    gulp.src(path.join(config.assets.src, 'assets/*'))
        .pipe(gulp.dest(config.assets.dest));
});