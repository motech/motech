var config = require('../config');
var gulp = require('gulp');
var path = require('path');
var mkdirp = require('mkdirp');
var nunjucks = require('nunjucks');
var fs = require('fs');
var argv = require('yargs').argv;

var paths = {
    src: path.join(config.root.src,'partials'),
    dest: path.join(config.root.dest,'partials')
}

function writeHeaderFile() {
  var fileName = 'header.html';
  var args = {
    staticPath: config.staticPath
  };

  if(argv.destPath) {
    paths.dest = argv.destPath
  }

  if(argv.staticPath) {
    args.staticPath = argv.staticPath;
    if (args.staticPath.indexOf("request.getContextPath") == 0){
        nunjucks.configure({ autoescape: false });
        args.staticPath = "<%=request.getContextPath()%>" + args.staticPath.replace("request.getContextPath", '');
    }
  }

  if(argv.jsp){
    args.jsp = true;
  }

  if(argv.fileName){
    fileName = argv.fileName;
  }

  mkdirp(paths.dest);
  fs.writeFile(
    path.join(paths.dest,fileName),
    nunjucks.render(path.join(paths.src,'header.html'), args),
    {flag:'w+'},
    function(err){
        if (err) {
            return console.log(err);
        }
    });
}

gulp.task('header', writeHeaderFile);