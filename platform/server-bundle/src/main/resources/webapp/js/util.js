'use strict';

/* Utilities */

Array.prototype.remove = function (from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};

Array.prototype.removeObject = function (element) {
    var i = this.indexOf(element);
    if (i != -1) {
        this.splice(i, 1);
    }
};

Array.prototype.isArray = true;

if (typeof String.prototype.startsWith != 'function') {
  String.prototype.startsWith = function (str){
    return this.slice(0, str.length) == str;
  };
}

if (typeof String.prototype.endsWith != 'function') {
  String.prototype.endsWith = function (str){
    return this.slice(-str.length) == str;
  };
}

String.prototype.format = function() {
  var args = arguments;
  return this.replace(/{(\d+)}/g, function(match, number) {
    return typeof args[number] != 'undefined'
      ? args[number]
      : match
    ;
  });
};

if(!Array.prototype.last) {
    Array.prototype.last = function() {
        return this[this.length - 1];
    }
}

String.prototype.insert = function (index, string) {
  if (index > 0)
    return this.substring(0, index) + string + this.substring(index, this.length);
  else
    return string + this;
};

Array.prototype.insert = function (index, item) {
  this.splice(index, 0, item);
};

function arraysEqual(arr1, arr2) {
    if(arr1.length !== arr2.length)
        return false;
    for(var i = arr1.length; i--;) {
        if(arr1[i] !== arr2[i])
            return false;
    }

    return true;
}

function toLocale(lang) {
    var locale = lang.replace("-", "_").split("_");
    return {
        language : locale[0],
        country : locale[1],
        variant : locale[2],
        withoutVariant : function() {
            return this.language + "_" + this.country;
        },
        fullName : function() {
            return this.language + "_" + this.country + "_" + this.variant;
        },
        toString : function() {
            if (this.language && this.country && this.variant) {
                return this.fullName();
            } else if (this.language && this.country) {
                return this.withoutVariant();
            } else {
                return this.language;
            }
        }
    };
}

function cloneObj(obj) {
    var clone = {};

    for (var i in obj) {
        if (typeof obj[i] == 'object') {
            clone[i] = cloneObj(obj[i]);
        } else {
            clone[i] = obj[i];
        }
    }

    return clone;
}
