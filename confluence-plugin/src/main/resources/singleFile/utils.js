const getFileFromTree = function (tree) {

    var file = tree

    while (file && file.type !== "DOCITEM") {
        file = file.children[0]
    }

    if (file) {
        return file.fullName
    } else {
        return file
    }

}

var downloadFile = function (uuid) {

    console.log("Downloading file")

    return Git4CApi.getTemporaryEditBranchForMacroUuid(uuid)
        .then(function (branch) {

            if (branch) {
                return Git4CApi.createTemporaryMacroForMacroAndBranch(uuid, branch)
                    .then(function (macroUuid) {
                        return getFileForTemporaryBranch(macroUuid, uuid)
                    })
            } else {
                return getFileForDefaultBranch(uuid)
            }

        })

};

var getFileForTemporaryBranch = function(newMacroUuid, originalMacroUuid) {

    return Git4CApi.getMacroDocumentationTree(newMacroUuid)
        .then(function (tree) {
            const file = getFileFromTree(tree)

            if (file) {
                return file
            } else {
                throw "File does not exist"
            }

        })
        .then(function (file) {
            return Git4CApi.getMacroDocumentationItem(newMacroUuid, file)
        })
        .then(function (documentationItem) {
            return Promise.all([
                Promise.resolve(newMacroUuid),
                Promise.resolve(documentationItem)
            ])
        })
        .catch(function (err) {
            console.log("Error while downloading file from temporary branch " + err)
            return getFileForDefaultBranch(originalMacroUuid)
        })
}

var getFileForDefaultBranch = function(uuid) {

    return Git4CApi.getMacroDocumentationTree(uuid)
        .then(function (tree) {
            return getFileFromTree(tree)
        })
        .then(function (file) {
            return Git4CApi.getMacroDocumentationItem(uuid, file)
        })
        .then(function (documentationItem) {
            return Promise.all([
                Promise.resolve(uuid),
                Promise.resolve(documentationItem)
            ])
        })
}

var getCurrentBranch = function(uuid) {
    return Git4CApi.getCurrentBranchForMacroUuid(uuid)
}

var getRepositoryName = function (macroUuid) {
    return Git4CApi.getRepositoryPathForMacroUuid(macroUuid)
}