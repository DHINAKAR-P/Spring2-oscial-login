// Relay Cursor Connections Specification: https://facebook.github.io/relay/graphql/connections.htm

package com.zimidy.api.layers.api.graphql.resolvers.aspects.relay

import com.zimidy.api.ClientError
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.Base64

typealias Cursor = String

data class PageInfo(val hasPreviousPage: Boolean, val hasNextPage: Boolean)

// We can't parametrize this interface, because graphql-java-tools doesn't support generics yet
interface Edge {
    val node: GraphQlNode
    val cursor: Cursor

    fun asString() = "Edge{GraphQlNode=$node, cursor=$cursor}"
}

// We can't parametrize this interface, because graphql-java-tools doesn't support generics yet
interface Connection {
    val edges: List<Edge>
    val pageInfo: PageInfo

    fun asString() = "Connection{edges=$edges, pageInfo=$pageInfo}"
}

abstract class ConnectionFactory<in N : GraphQlNode, E : Edge, out C : Connection>(allGraphQlNodes: List<N>) {

    private val allEdges: List<E> = allGraphQlNodes.mapIndexedTo(ArrayList()) { index, node ->
        createEdge(node, createCursor(index))
    }

    // Args order differs from official specs, because it makes more sense for a user of this class, in my opinion.
    /**
     * You can ask this function to return a cursor, that will represent one of the following conditions:
     * - elements after [after] or before [before] cursor
     * - first [first] or last [last] elements
     * - elements between [after] and [before] cursor
     * - first [first] or last [last] elements after [after] or before [before] cursor
     * - first [first] or last [last] elements between [after] and [before] cursors
     *
     * Note: Including a value for both [first] and [last] is strongly discouraged, as it may lead to confusions.
     */
    fun create(after: Cursor?, before: Cursor?, first: Int?, last: Int?): C {
        val beforeIndex = getIndexFromCursor(before)
        val afterIndex = getIndexFromCursor(after)
        val edges = edgesToReturn(before = beforeIndex, after = afterIndex, first = first, last = last)
        val hasPreviousPage = hasPreviousPage(before = beforeIndex, after = afterIndex, last = last)
        val hasNextPage = hasNextPage(before = beforeIndex, after = afterIndex, first = first)
        return createConnection(edges, PageInfo(hasPreviousPage = hasPreviousPage, hasNextPage = hasNextPage))
    }

    // EdgesToReturn(allEdges, before, after, first, last)
    private fun edgesToReturn(before: Int? = null, after: Int? = null, first: Int? = null, last: Int? = null): List<E> {
        // Let edges be the result of calling ApplyCursorsToEdges(allEdges, before, after).
        var edges = applyCursorsToEdges(before = before, after = after)
        // If first is set:
        if (first != null) {
            // If first is less than 0:
            if (first < 0) {
                // Throw an error.
                throw ClientError("The 'first' argument must be a non-negative integer.")
            }
            // If edges has length greater than first:
            if (edges.size > first) {
                // Slice edges to be of length first by removing edges from the end of edges.
                edges = edges.dropLast(edges.size - first)
            }
        }
        // If last is set:
        if (last != null) {
            // If last is less than 0:
            if (last < 0) {
                // Throw an error.
                throw ClientError("The 'last' argument must be a non-negative integer.")
            }
            // If edges has length greater than than last:
            if (edges.size > last) {
                // Slice edges to be of length last by removing edges from the start of edges.
                edges = edges.drop(edges.size - last)
            }
        }
        // Return edges.
        return edges
    }

    // ApplyCursorsToEdges(allEdges, before, after)
    private fun applyCursorsToEdges(before: Int? = null, after: Int? = null): List<E> {
        // Initialize edges to be allEdges.
        val edges = allEdges.toMutableList<E?>()
        // If after is set:
        if (after != null) {
            // Let afterEdge be the edge in edges whose cursor is equal to the after argument.
            val afterEdge = edges.elementAtOrNull(after)
            // If afterEdge exists:
            if (afterEdge != null) {
                // Remove all elements of edges before and including afterEdge.
                edges.forEachIndexed { index, _ -> if (index <= after) edges[index] = null }
            }
        }
        // If before is set:
        if (before != null) {
            // Let beforeEdge be the edge in edges whose cursor is equal to the before argument.
            val beforeEdge = edges.elementAtOrNull(before)
            // If beforeEdge exists:
            if (beforeEdge != null) {
                // Remove all elements of edges after and including beforeEdge.
                edges.forEachIndexed { index, _ -> if (index >= before) edges[index] = null }
            }
        }
        // Return edges.
        return edges.filterNotNull()
    }

    // HasPreviousPage(allEdges, before, after, first, last)
    private fun hasPreviousPage(before: Int? = null, after: Int? = null, last: Int? = null): Boolean {
        // If last is set:
        if (last != null) {
            // Let edges be the result of calling ApplyCursorsToEdges(allEdges, before, after).
            val edges = applyCursorsToEdges(before = before, after = after)
            // If edges contains more than last elements return true, otherwise false.
            return edges.size > last
        }
        // If after is set:
        if (after != null) {
            // If the server can efficiently determine that elements exist prior to after, return true.
            if (after > 0) return true
        }
        // Return false.
        return false
    }

    // HasNextPage(allEdges, before, after, first, last)
    private fun hasNextPage(before: Int? = null, after: Int? = null, first: Int? = null): Boolean {
        // If first is set:
        if (first != null) {
            // Let edges be the result of calling ApplyCursorsToEdges(allEdges, before, after).
            val edges = applyCursorsToEdges(before = before, after = after)
            // If edges contains more than first elements return true, otherwise false.
            return edges.size > first
        }
        // If before is set:
        if (before != null) {
            // If the server can efficiently determine that elements exist following before, return true.
            if (before < allEdges.size - 1) return true
        }
        // Return false.
        return false
    }

    protected abstract fun createConnection(edges: List<E>, pageInfo: PageInfo): C

    protected abstract fun createEdge(node: N, cursor: Cursor): E

    private fun getIndexFromCursor(cursor: Cursor?): Int? {
        if (cursor == null) {
            return null
        }

        val bytes: ByteArray
        try {
            bytes = Base64.getDecoder().decode(cursor)
        } catch (e: IllegalArgumentException) {
            throw ClientError("The cursor ('$cursor') is not in base64 format.")
        }

        val string = String(bytes, StandardCharsets.UTF_8)
        try {
            return Integer.parseInt(string)
        } catch (e: NumberFormatException) {
            throw ClientError("The cursor ('$cursor') was not created by ${javaClass.simpleName}.")
        }
    }

    private fun createCursor(index: Int): Cursor {
        val string = Integer.toString(index)
        val bytes = string.toByteArray(StandardCharsets.UTF_8)
        return Base64.getEncoder().encodeToString(bytes)
    }
}
