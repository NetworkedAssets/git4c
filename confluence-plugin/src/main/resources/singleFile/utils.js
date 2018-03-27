var downloadFile = function (uuid2) {

    var uuid = uuid2

    console.log("Download file")

    const getFileFromTree = function (tree) {

        var file = tree

        while (file.type !== "DOCITEM") {
            file = file.children[0]
        }

        return file.fullName

    }

    return Git4CApi.getTemporaryEditBranchForMacroUuid(uuid)
        .then(function (branch) {

            if (branch) {
                return Git4CApi.createTemporaryMacroForMacroAndBranch(uuid, branch)
            } else {
                return Promise.resolve(uuid)
            }

        })
        .then(function (uuid) {

            const p = Git4CApi.getMacroDocumentationTree(uuid)
                .then(function (value) {
                    return getFileFromTree(value)
                })
                .then(function (file) {
                    return Git4CApi.getMacroDocumentationItem(uuid, file)
                })


            return Promise.all([
                Promise.resolve(uuid),
                p
            ])

        })
        .then(function (promises) {
            return promises
        })

};

var getCurrentBranch = function(uuid) {
    return Git4CApi.getCurrentBranchForMacroUuid(uuid)
}

var getRepositoryName = function (macroUuid) {
    return Git4CApi.getRepositoryPathForMacroUuid(macroUuid)
}