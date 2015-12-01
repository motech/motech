/**
* Gulp Packages
*/

// General
var gulp = require('gulp');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var lib = require('bower-files')();

// Static: Compress JS files into motech.js
gulp.task('js', function () {
    var files = lib.ext('js').files; // libraries from bower
    files.push('src/js/*.js'); // common files

    gulp.src(files)
        .pipe(sourcemaps.init())
          .pipe(concat('motech.js'))
        .pipe(sourcemaps.write())
        .pipe(gulp.dest('build/assets/js'))
        .pipe(uglify())
        .pipe(rename('motech.min.js'))
        .pipe(gulp.dest('build/assets/js'));
});

gulp.task('css', function () {
    var files = lib.ext('css').files;
    files.push('src/css/**/*.css');
    gulp.src(files)
        .pipe(concat('motech.css'))
        .pipe(gulp.dest('build/assets/css'));
});

gulp.task('images', function () {
    gulp.src('src/img/*')
        .pipe(gulp.dest('build/assets/img'));
});

gulp.task('fonts', function () {
    gulp.src('src/fonts/*')
        .pipe(gulp.dest('build/assets/fonts'));
})

gulp.task('default', function () {
    gulpSequence('js', 'css', 'fonts', 'images')();
});