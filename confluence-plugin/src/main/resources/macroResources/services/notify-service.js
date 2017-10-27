var NotifyService = (function () {
    return {

        error: function(title, message){
            AJSC.flag({
                type: 'error',
                title: title,
                close: 'auto',
                persistent: false,
                body: '<p>' + message + '</p>'
            });

        },

        info: function(title, message){
            AJSC.flag({
                type: 'info',
                title: title,
                close: 'auto',
                persistent: false,
                body: '<p>' + message + '</p>'
            });

        },

        success: function(title, message) {
            AJSC.flag({
                type: 'success',
                title: title,
                close: 'auto',
                persistent: false,
                body: '<p>' + message + '</p>'
            });
        },

        persistent: function(title, body) {
            AJSC.flag({
                type: "info",
                title: title,
                close: "manual",
                body: body
            })

        }

    }
})();
