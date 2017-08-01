//AJS Compat
var AJSC = {}

AJSC.flag = function (data) {
    if (AJS.flag) {
        AJS.flag(data)
    } else {
        require(['aui/flag'], function(flag) {
            flag(data);
        })
    }
}

