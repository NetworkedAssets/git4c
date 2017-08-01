var ParamsService = (function () {

    let params = {
        "uuid": ''
    };

    var isConfluence = function () {
        return AJS && AJS.Data;
    };

    return {
        //5.8.2 fix - before AJS.toInit, AJS.params is empty
        initialize: () => {
            if (isConfluence()) {
                params = JSON.parse(AJS.params.macroParamsJson);
            } else {
                params = {
                    "uuid": "1"
                }
            }
            params.initialUuid = params.uuid
        },
        setUuid: (id) => {
            params.uuid = id
        },
        getParams: () => {
            return params;
        },
        getInitialUuid: () => {
            return params.initialUuid
        },
        getUuid: () => {
            return ParamsService.getParams().uuid;
        },
        isConfluence: function () {
            return isConfluence()
        }
    };

})();