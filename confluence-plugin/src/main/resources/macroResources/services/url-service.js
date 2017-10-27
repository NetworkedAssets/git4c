'use strict';

var UrlService = function () {

    var parseArgs = function parseArgs(args) {
        return args && args.length ? '/' + args.map(function (arg) {
                if (arg) {
                    return encodeURI(arg.toString());
                } else {
                    return '';
                }
            }).join('/') : '';
    };

    return {
        getRestUrl: function getRestUrl() {
            for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
                args[_key] = arguments[_key];
            }

            return UrlService.getBaseUrl() + parseArgs(args);
        },
        getBaseUrl: function getBaseUrl() {
            if (AJS && AJS.Data) {
                //todo verify
                return AJS.Data.get('base-url') + '/rest/doc/1.0';
            } else {
                return 'http://localhost:8080/rest';
            }
        }
    };
}();