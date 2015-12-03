var config = require('../config.js');
var gulp = require('gulp');
var path = require('path');
var watch = require('gulp-watch');
var gulpSequence = require('gulp-sequence');

gulp.task('watch', function(){
    watch(path.join(config.root.src,'**/*'), function(){
        gulpSequence('js', 'css', 'fonts', 'images')();
    });
});