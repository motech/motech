var config = require('../config');
var gulp = require('gulp');
var path = require('path');

var paths = {
    src: path.join(config.assets.src, 'fonts/*'),
    dest: path.join(config.assets.dest, 'fonts')
}

gulp.task('fonts', function () {
    gulp.src([
        paths.src,
        './bower_components/bootstrap/fonts/*',
        './bower_components/font-awesome/fonts/*'
    ]).pipe(gulp.dest(paths.dest));
})