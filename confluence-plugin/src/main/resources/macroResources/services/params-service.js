var ParamsService = (function () {

    var params = {
        "uuid": ''
    };

    var isConfluence = function () {
        return AJS && AJS.Data;
    };

    return {
        //5.8.2 fix - before AJS.toInit, AJS.params is empty
        initialize: function() {
            if (isConfluence()){
                params = JSON.parse(AJS.params.macroParamsJson);
            } else {
                params = {
                    "uuid": "1"
                }
            }
            params.initialUuid = params.uuid
        },
        setUuid: function(id){
            params.uuid = id
        },
        getParams: function() {
            return params;
        },
        getInitialUuid: function() {
            return params.initialUuid
        },
        getUuid: function() {
            return ParamsService.getParams().uuid;
        },
        isConfluence: function() {
            return isConfluence()
        }
    };

})();