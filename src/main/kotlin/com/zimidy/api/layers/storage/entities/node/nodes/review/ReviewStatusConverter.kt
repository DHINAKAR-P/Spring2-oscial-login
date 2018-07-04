package com.zimidy.api.layers.storage.entities.node.nodes.review

import org.neo4j.ogm.typeconversion.EnumStringConverter

class ReviewStatusConverter : EnumStringConverter(ReviewStatus::class.java)
