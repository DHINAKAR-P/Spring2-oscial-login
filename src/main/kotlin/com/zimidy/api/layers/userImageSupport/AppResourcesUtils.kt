package com.zimidy.api.layers.userImageSupport

import lombok.NonNull
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.lang.Nullable
import java.io.IOException
import java.io.InputStream
import java.util.Arrays
import java.util.Properties
import java.util.stream.Stream

object AppResourcesUtils {

    var PROPERTIES_FILE_NAME = "application.yml"
    var MESSAGES_FILE_NAME = "messages.properties"

    var RESOURCE_LOADER = PathMatchingResourcePatternResolver()
    var DEVELOPMENT_ENVIRONMENT = java.lang.Boolean.valueOf(getProperty("app.environment.development"))

    private var properties: Properties? = Properties()
    private var messages: Properties? = Properties()

    @NonNull
    fun getMessageOrThrow(key: String): String {
        val value = getMessage(key)
        if (value == null) {
            val format = "Message '%s' must be specified in '%s' file."
            val message = String.format(format, key, MESSAGES_FILE_NAME)
            throw RuntimeException(message)
        }
        return value
    }

    @NonNull
    fun getPropertyOrThrow(key: String): String {
        val value = getProperty(key)
        if (value == null) {
            val format = "Property '%s' must be specified in '%s' file."
            val message = String.format(format, key, PROPERTIES_FILE_NAME)
            throw RuntimeException(message)
        }
        return value
    }

    @Nullable
    fun getMessage(key: String): String? {
        return getMessage(key, null)
    }

    @Nullable
    fun getProperty(key: String): String? {
        return getProperty(key, null)
    }

    fun getMessage(key: String, defaultValue: String?): String {
        return getMessages().getProperty(key, defaultValue)
    }

    fun getProperty(key: String, defaultValue: String?): String {
        return getProperties().getProperty(key, defaultValue)
    }

    @NonNull
    fun getMessages(): Properties {

        if (messages == null || DEVELOPMENT_ENVIRONMENT) {
            messages = getResourceAsProperties(MESSAGES_FILE_NAME)
        } else messages

        return messages as Properties
    }

    @NonNull
    fun getProperties(): Properties {

        if (properties == null || DEVELOPMENT_ENVIRONMENT) {
            properties = getResourceAsProperties(PROPERTIES_FILE_NAME)
        } else properties

        return properties as Properties
    }

    @NonNull
    private fun getResourceAsProperties(location: String): Properties {
        val properties = Properties()
        try {
            getResourceAsStream(location).use({ `is` -> properties.load(`is`) })
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return properties
    }

    @NonNull
    fun getResourceAsStream(@NonNull location: String): InputStream {
        val resource = RESOURCE_LOADER.getResource(location)
        if (!resource.exists()) {
            val format = "No resource is found by the location: '%s'"
            val message = String.format(format, location)
            throw RuntimeException(message)
        }
        try {
            return resource.inputStream
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun getResourceFilenames(locationPattern: String): Stream<String> {
        val resources: Array<Resource>
        try {
            resources = RESOURCE_LOADER.getResources(locationPattern)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return Arrays.stream(resources).map(({ it.getFilename() }))
    }
}
