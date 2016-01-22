var config = require('../config');
var gulp = require('gulp');
var lib = require('bower-files')();
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var rename = require('gulp-rename');
var sourcemaps = require('gulp-sourcemaps');
var path = require('path');

var paths = {
    src: path.join(config.assets.src, '**/*.js'),
    dest: path.join(config.assets.dest, 'js')
}

// Static: Compress JS files into motech.js
gulp.task('js', function () {
    var files = lib.ext('js').files; // libraries from bower
    files.push(paths.src); // common files

    return gulp.src(files)
        .pipe(sourcemaps.init())
          .pipe(concat('motech.js'))
        .pipe(sourcemaps.write())
        .pipe(gulp.dest(paths.dest))
});

gulp.task('uglify', function (){
    return gulp.src(path.join(paths.dest, 'motech.js'))
        .pipe(uglify())
        .pipe(rename('motech.min.js'))
        .pipe(gulp.dest(paths.dest));
});