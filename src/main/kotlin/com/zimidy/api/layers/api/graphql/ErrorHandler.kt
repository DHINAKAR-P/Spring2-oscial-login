package com.zimidy.api.layers.api.graphql

import com.zimidy.api.AppProperties
import com.zimidy.api.AppProperties.Env
import com.zimidy.api.ClientError
import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError
import graphql.servlet.DefaultGraphQLErrorHandler
import org.springframework.stereotype.Component

@Component
class ErrorHandler(val app: AppProperties) : DefaultGraphQLErrorHandler() {

    override fun filterGraphQLErrors(errors: MutableList<GraphQLError>?): MutableList<GraphQLError> {
        val filteredErrors = super.filterGraphQLErrors(errors).map { error ->
            if (error is ExceptionWhileDataFetching && error.exception is ClientError) {
                return@map DataFetchingGraphQlClientError(error)
            }
            return@map error
        }
        return filteredErrors.toMutableList()
    }

    override fun isClientError(error: GraphQLError?): Boolean {
        if (app.env == Env.DEV) return true // show all errors to a developer
        if (error is ExceptionWhileDataFetching && error.exception is ClientError) return true
        return super.isClientError(error)
    }
}

class DataFetchingGraphQlClientError(private val error: ExceptionWhileDataFetching) : GraphQLError by error {
    override fun getMessage(): String? = error.exception.message
}
