(function ($) { // this closure helps us keep our variables to ourselves.
// This pattern is known as an "iife" - immediately invoked function expression

    // form the URL
    const url = AJS.contextPath() + "/rest/doc/1.0";

    let stage = 0;

    AJS.toInit(function () {
        const button = $("#remove_data_button");

        button.click(function () {

            if (stage === 0) {
                stage = 1
                button.text("ARE YOU SURE?")
            } else if (stage === 1) {
                $.ajax({
                    url: url + "/documentation/",
                    type: 'DELETE',
                }).done(function () {
                    AJSC.flag({
                        type: "info",
                        title: "Data removal completed successfully"
                    })
                    stage = 0;
                    button.text("Clean data")
                })
            }
        })
    })

})(AJS.$ || jQuery);
