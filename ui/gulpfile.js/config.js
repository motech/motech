var config = require('../config');
var argv = require('yargs').argv;

if (argv.dest) {
    config.root.dest = argv.dest;
}

module.exports = config;