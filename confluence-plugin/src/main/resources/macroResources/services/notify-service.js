var NotifyService = (function () {
    return {

        error: (title, message) => {
            AJSC.flag({
                type: 'error',
                title: title,
                close: 'auto',
                persistent: false,
                body: '<p>' + message + '</p>'
            });

        },

        info(title, message) {
            AJSC.flag({
                type: 'info',
                title: title,
                close: 'auto',
                persistent: false,
                body: '<p>' + message + '</p>'
            });

        },

        success(title, message) {
            AJSC.flag({
                type: 'success',
                title: title,
                close: 'auto',
                persistent: false,
                body: '<p>' + message + '</p>'
            });
        },

        persistent(title, body) {
            AJSC.flag({
                type: "info",
                title: title,
                close: "manual",
                body: body
            })

        }

    }
})();
