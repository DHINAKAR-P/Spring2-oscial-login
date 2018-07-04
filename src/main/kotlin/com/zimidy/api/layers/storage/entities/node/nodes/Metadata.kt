package com.zimidy.api.layers.storage.entities.node.nodes

import com.zimidy.api.layers.storage.entities.node.Node

data class Metadata(
    var buildTimestamp: String = "",
    var commitTimestamp: String = "",
    var commitBranch: String = "",
    var commitRevision: String = ""
) : Node()
