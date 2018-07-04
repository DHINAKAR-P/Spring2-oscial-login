package com.zimidy.api.layers.api.graphql.resolvers

import com.zimidy.api.AccessibleBySuperAdmin
import com.zimidy.api.AccessibleByUsers
import com.zimidy.api.AppProperties
import com.zimidy.api.layers.api.graphql.resolvers.nodes.UserNode
import com.zimidy.api.layers.storage.repositories.node.nodes.MetadataRepository
import org.springframework.stereotype.Component

@Component
class QueryResolver(
    private val app: AppProperties,
    private val metadataRepository: MetadataRepository
) : AbstractQueryResolver() {

    @AccessibleBySuperAdmin
    fun metadata(): Metadata {
        val neo4j = metadataRepository.load()
        val api = app.metadata
        return Metadata(
                neo4j = ModuleMetadata(
                        build = BuildMetadata(timestamp = neo4j.buildTimestamp),
                        commit = CommitMetadata(
                                timestamp = neo4j.commitTimestamp,
                                branch = neo4j.commitBranch,
                                revision = neo4j.commitRevision
                        )
                ),
                api = ModuleMetadata(
                        build = BuildMetadata(timestamp = api.build.timestamp),
                        commit = CommitMetadata(
                                timestamp = api.commit.timestamp,
                                branch = api.commit.branch,
                                revision = api.commit.revision
                        )
                )
        )
    }

    @AccessibleByUsers
    fun viewer() = UserNode(getAuthenticatedUserOrThrow())
}

@Component
class MutationResolver : AbstractMutationResolver() {
    fun dummyMutation(): Boolean = true
}

data class Metadata(val neo4j: ModuleMetadata, val api: ModuleMetadata)

data class ModuleMetadata(val build: BuildMetadata, val commit: CommitMetadata)

data class BuildMetadata(val timestamp: String)

data class CommitMetadata(val timestamp: String, val branch: String, val revision: String)
