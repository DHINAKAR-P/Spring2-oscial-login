package com.zimidy.api

import org.apache.commons.lang3.text.StrSubstitutor


import lombok.NonNull
import org.springframework.web.multipart.MultipartFile
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Field
import java.net.URL
import java.nio.channels.Channels
import java.util.*
import java.util.stream.Collector

/**
 * Utilities, which are intended to be used anywhere across the project (on any layer)
 */
object Utils {

    @NonNull
    fun getDataFromUrl(@NonNull dataUrl: String): ByteArray {
        if (!isDataUrl(dataUrl)) {
            throw RuntimeException("A provided URL is not a data URL.")
        }
        val dataEncodedAsBase64 = dataUrl.substring(dataUrl.indexOf(",") + 1)
        return java.util.Base64.getDecoder().decode(dataEncodedAsBase64)
    }

    fun isDataUrl(@NonNull url: String): Boolean {
        return url.startsWith("data:")
    }

  /*  fun format(format: String, vararg parameters: Any): String {
        return format(format, arrayToMap(parameters))
    }
*/
    fun format(format: String, parametersMap: Map<String, String>): String {
        val strSubstitutor = StrSubstitutor(parametersMap, "{", "}")
        return strSubstitutor.replace(format)
    }

    @NonNull
    fun arrayToMap(@NonNull array: Array<Any>?): Map<String, String> {
        if (array == null) {
            return emptyMap()
        }
        val parametersMap = HashMap<String, String>()
        var n = 0
        var v = 1
        while (n < array.size) { // n - name index, v - value index
            val name = array[n].toString()
            if (v >= array.size) {
                val errorFormat = "No value for the '%s' key."
                val errorMessage = String.format(errorFormat, name)
                throw RuntimeException(errorMessage)
            }
            val value = array[v].toString()
            parametersMap[name] = value
            n += 2
            v = n + 1
        }
        return parametersMap
    }

    fun checkParamsAllOrNone(paramNames: String, vararg params: Any) {
        if (!isParamsAllOrNone(*params)) {
            val errorMessage = "Params (%s) must be provided either all or none."
            throw RuntimeException(String.format(errorMessage, paramNames))
        }
    }

    fun isParamsAllOrNone(vararg params: Any): Boolean {
        return isParamsAll(*params) || isParamsNone(*params)
    }

    fun isParamsNone(vararg params: Any): Boolean {
        var none = true
        for (argument in params) {
            if (argument != null) {
                none = false
            }
        }
        return none
    }

    fun isParamsAll(vararg params: Any): Boolean {
        var all = true
        for (argument in params) {
            if (argument == null) {
                all = false
            }
        }
        return all
    }

    fun <T> singletonCollector(): Collector<T, *, T> {
        return singletonCollector( )
    }

   /* fun <T> singletonCollector(required: Boolean): Collector<T, *, T> {
        return Collectors.collectingAndThen<T, Any, List<T>, T>(
                Collectors.toList(),
                { list ->
                    val size = list.size
                    if (size == 1) {
                        return   list . get 0
                    }
                    if (size == 0 && !required) {
                        return  null
                    }
                    var messageFormat = "Illegal size of collection: %s. "
                    if (required) {
                        messageFormat += "Should contain exactly one element."
                    } else {
                        messageFormat += "Should not contain more than one element."
                    }
                    val message = String.format(messageFormat, size)
                    throw IllegalStateException(message)
                }
        )
    }*/
    /*fun getMessageWithCause(@NonNull throwable: Throwable): String {
        val message = throwable.message
        val cause = throwable.cause ?: return message
        return String.format("%s: %s", message, cause.message)
    }*/

    /**
     * Returns all declared fields of the whole type hierarchy, having the annotation type.
     * This method is able to find repeatable annotations via a container annotation.
     *
     * @param type           the root of the type hierarchy
     * @param annotationType the type of the annotation, which must be present on a field
     * @return found fields
     */

  /*  fun getAllDeclaredAnnotatedFields(type: Class<*>, annotationType: Class<  Annotation>): List<Field> {
        return getAllDeclaredFields(type).stream()
                .filter { field -> field.getAnnotationsByType<Annotation>(annotationType).size > 0 }
                .collect<List<Field>, Any>(Collectors.toList())
    }
*/
    /**
     * Returns all declared fields of the whole type hierarchy.
     *
     * @param type the root of the type hierarchy
     * @return found fields
     */
    fun getAllDeclaredFields(type: Class<*>): List<Field> {
        val fields = ArrayList<Field>()
        var currentType: Class<*>? = type
        while (currentType != null) {
            fields.addAll(Arrays.asList(*currentType.declaredFields))
            currentType = currentType.superclass
        }
        return fields
    }

    fun saveDataOnDisk(`is`: InputStream, path: String) {
        try {
            val rbc = Channels.newChannel(`is`)
            val fos = FileOutputStream(path)
            fos.channel.transferFrom(rbc, 0, java.lang.Long.MAX_VALUE)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    fun asInputStream(multipartFile: MultipartFile): InputStream {
        val inputStream: InputStream
        try {
            inputStream = multipartFile.inputStream
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        return inputStream
    }

    fun asInputStream(url: String): InputStream {
        val _url: URL
        try {
            _url = URL(url)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        return asInputStream(_url)
    }

    fun asInputStream(url: URL): InputStream {
        val inputStream: InputStream
        try {
            inputStream = url.openStream()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        return inputStream
    }
}
