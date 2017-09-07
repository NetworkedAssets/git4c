var Git4CUtils = {

    filesWithoutLineNumbers: ["puml", "png", "jpeg", "jpg", "svg", "md"],

    hasLines: function (file) {
        let lines = true;
        Git4CUtils.filesWithoutLineNumbers.forEach((ext) => {
            if (file.endsWith("." + ext)) {
                lines = false
            }
        });
        return lines
    }

};