var config = require('../config');
var gulp = require('gulp');
var lib = require('bower-files')();
var concat = require('gulp-concat');
var bless = require('gulp-bless');
var sass = require('gulp-sass');
var path = require('path');

var paths = {
    src: path.join(config.assets.src, '**/*.scss'),
    dest: path.join(config.assets.dest, 'css')
};

gulp.task('css', function () {
    var files = lib.ext('css').files;
    files.push(paths.src);
    gulp.src(files)
        .pipe(concat('motech.css'))
        .pipe(sass().on('error', sass.logError))
        .pipe(bless())
        .pipe(gulp.dest(paths.dest));
});