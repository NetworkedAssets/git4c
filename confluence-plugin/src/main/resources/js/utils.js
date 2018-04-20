var Git4CUtils = {

    filesWithoutLineNumbers: ["puml", "png", "jpeg", "jpg", "svg", "md", "adoc"],
    filesWithSourceCode: ["puml", "svg", "md", "adoc", "ad", "asciidoc", "asc"],

    hasLines: function (file) {
        var lines = true;
        Git4CUtils.filesWithoutLineNumbers.forEach(function (ext) {
            if (file.endsWith("." + ext)) {
                lines = false
            }
        });
        return lines
    },

    hasSourceCode: function (file) {
        var sourceCode = false;
        Git4CUtils.filesWithSourceCode.forEach(function (ext) {
            if (file.endsWith("." + ext)) {
                sourceCode = true
            }
        });
        return sourceCode
    },

    scrollTo: function (container, id) {
        const el = Git4CUtils.findElementToScrollTo(container, id);
        window.scrollTo(0, el.offsetTop)
    },

    findElementToScrollTo: function (container, id) {

        const document = $(container);

        let top;

        const findResult = document.find("#" + id + ", [name = " + id + "]")

        // if (findResult.length) {
        //     top = findResult[0]
        // } else {
        //     top = document.find("[name = " + id + "]")[0]
        // }

        // return top;

        return findResult[0]

    }

};