var config = require('../config');
var gulp = require('gulp');
var gulpSequence = require('gulp-sequence');

gulp.task('default', function () {
    gulpSequence('assets', 'js', 'uglify', 'css', 'fonts', 'images')();
});