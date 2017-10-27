var Git4CUtils = {

    filesWithoutLineNumbers: ["puml", "png", "jpeg", "jpg", "svg", "md", "adoc"],

    hasLines: function (file) {
        var lines = true;
        Git4CUtils.filesWithoutLineNumbers.forEach(function (ext) {
            if (file.endsWith("." + ext)) {
                lines = false
            }
        });
        return lines
    }

};