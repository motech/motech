var config = require('../config');
var gulp = require('gulp');
var lib = require('bower-files')();
var concat = require('gulp-concat');
var path = require('path');

var paths = {
    src: path.join(config.assets.src, '**/*.css'),
    dest: path.join(config.assets.dest, 'css')
};

gulp.task('css', function () {
    var files = lib.ext('css').files;
    files.push(paths.src);
    gulp.src(files)
        .pipe(concat('motech.css'))
        .pipe(gulp.dest(paths.dest));
});