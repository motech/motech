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
