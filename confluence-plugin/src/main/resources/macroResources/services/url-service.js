var UrlService = (function () {

    var parseArgs = function (args) {
        return ((args && args.length) ? ('/' + args.map((arg) => {
            if (arg) {
                return encodeURI(arg.toString());
            } else {
                return '';
            }
        }).join('/')) : '');
    };

    return {
        getRestUrl: (...args) => {
            return UrlService.getBaseUrl() + parseArgs(args);
        },
        getBaseUrl: () => {
            if (AJS && AJS.Data) { //todo verify
                return AJS.Data.get('base-url') + '/rest/doc/1.0';
            } else {
                return 'http://localhost:8080/rest';
            }
        }
    };
})();