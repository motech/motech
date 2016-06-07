(function(open) {
/*
 * Catches all XMLHttpReqests, and if 302 to /login, sets window location to that page
 */
  XMLHttpRequest.prototype.open = function(method, url, async, user, pass) {
    var xhr = this;
    this.onload = function(){
        if(window.location.pathname.indexOf("server/login") < 0 && (xhr.status === 302 || xhr.status === 401)){
        	document.location.reload();
        }
    }
    open.call(this, method, url, async, user, pass);
  };
})(XMLHttpRequest.prototype.open);