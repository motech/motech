var config = require('../config');
var gulp = require('gulp');
var path = require('path');

var paths = {
    src: path.join(config.assets.src, 'img/**/*'),
    dest: path.join(config.assets.dest, 'img')
}

gulp.task('images', function () {
    gulp.src(paths.src)
        .pipe(gulp.dest(paths.dest));
});