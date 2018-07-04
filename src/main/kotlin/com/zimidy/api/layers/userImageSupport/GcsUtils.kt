package com.zimidy.api.layers.userImageSupport

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.InputStreamContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.storage.Storage
import com.google.api.services.storage.StorageScopes
import com.google.api.services.storage.model.ObjectAccessControl
import com.google.api.services.storage.model.Objects
import com.google.api.services.storage.model.StorageObject
import com.zimidy.api.layers.userSupport.Utils
import lombok.NonNull
import lombok.extern.slf4j.Slf4j
import org.springframework.lang.Nullable
import org.springframework.util.StringUtils
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.Collections
import java.util.UUID
import java.util.stream.Collectors
import java.util.stream.Stream

@Slf4j
object GcsUtils {
    private val MIME_JPG = "image/jpeg"
    private val BASE_URL = "https://storage.googleapis.com/"
    private val BUCKET = AppResourcesUtils.getPropertyOrThrow("gcs.bucket")
    private val CREDENTIAL_JSON = "gcs_credential.json"
    private val ENVIRONMENT_CLASSIFIER: Char
    private val STORAGE: Storage

    init {

        // Setup environment classifier:
        val envClassifier = AppResourcesUtils.getPropertyOrThrow("gcs.environment_classifier_char")
        if (envClassifier.length != 1) {
            throw RuntimeException("Environment classifier must be represented by a single character.")
        }
        ENVIRONMENT_CLASSIFIER = envClassifier.get(0)

        // Setup storage:
        val gcsCredentialStream = AppResourcesUtils.getResourceAsStream(CREDENTIAL_JSON)
        try {
            val transport = GoogleNetHttpTransport.newTrustedTransport()
            val jsonFactory = JacksonFactory()
            var credential = GoogleCredential.fromStream(gcsCredentialStream, transport, jsonFactory)
            if (credential.createScopedRequired()) {
                val scopes = StorageScopes.all()
                credential = credential.createScoped(scopes)
            }
            STORAGE = Storage.Builder(transport, jsonFactory, credential).setApplicationName("Zimidy").build()
        } catch (e: Exception) {
            throw RuntimeException("Unable to build storage.", e)
        }
    }

    @NonNull
    fun listObjectUrls(@Nullable vararg objectTypes: ObjectType): List<String> {
        return listObjects(ENVIRONMENT_CLASSIFIER, *objectTypes).stream()
            .map<String>({ getObjectUrl(it) })
            .collect(Collectors.toList())
    }

    @NonNull
    fun listObjects(environmentClassifier: Char?, @Nullable vararg objectTypes: ObjectType): List<StorageObject> {
        val storageObjects = ArrayList<StorageObject>()
        try {
            val listRequest = STORAGE.objects().list(BUCKET)
            var objects: Objects
            do {
                objects = listRequest.execute()
                val items = objects.getItems() ?: break
                for (`object` in items) {
                    val objectName = `object`.getName()
                    if (environmentClassifier != null && environmentClassifier != getEnvironmentClassifier(objectName)) {
                        continue
                    }
                    if (objectTypes != null && objectTypes.size > 0) {
                        if (!(objectTypes).contains(getObjectType(objectName))) {
                            continue
                        }
                    }
                    storageObjects.add(`object`)
                }
                listRequest.setPageToken(objects.getNextPageToken())
            } while (null != objects.getNextPageToken())
        } catch (e: IOException) {
            throw RuntimeException("Failed to list objects.", e)
        }

        return storageObjects
    }

    fun uploadDataAsObject(@NonNull url: String, @NonNull type: ObjectType): String {
        if (url.startsWith(BASE_URL + BUCKET)) { // the URL looks as if an object was already uploaded
            return url
        }
        val dataInputStream: InputStream
        if (Utils.isDataUrl(url)) {
            val data = Utils.getDataFromUrl(url)
            dataInputStream = ByteArrayInputStream(data)
        } else {
            dataInputStream = Utils.asInputStream(url)
        }
        return uploadDataAsObject(dataInputStream, type)
    }

    /**
     * Uploads data as an object onto a bucket.
     *
     * @return Object URL
     */
    @JvmOverloads
    fun uploadDataAsObject(@NonNull data: InputStream, @NonNull type: ObjectType, @NonNull mime: String = type.mime): String {
        val name = generateObjectName(type)
        val objectAccessControl = ObjectAccessControl().setEntity("allUsers").setRole("READER")
        var `object` = StorageObject()
            .setName(name)
            .setAcl(Collections.singletonList(objectAccessControl))
        try {
            `object` = STORAGE.objects().insert(BUCKET, `object`, InputStreamContent(mime, data)).execute()
        } catch (e: IOException) {
            throw RuntimeException("Failed to upload data as object.", e)
        }

        return getObjectUrl(`object`)
    }

    fun deleteObject(@NonNull objectName: String) {
        try {
            STORAGE.objects().delete(BUCKET, objectName).execute()
        } catch (e: IOException) {
            val format = "Failed to delete an object: %s."
            val message = String.format(format, objectName)
            throw RuntimeException(message, e)
        }
    }

    private fun getObject(`object`: String): StorageObject {
        try {
            return STORAGE.objects().get(BUCKET, `object`).execute()
        } catch (e: IOException) {
            val format = "Failed to get an object: %s."
            val message = String.format(format, `object`)
            throw RuntimeException(message, e)
        }
    }

    @NonNull
    private fun getObjectUrl(@NonNull `object`: StorageObject): String {
        return BASE_URL + `object`.getBucket() + "/" + `object`.getName()
    }

    private fun getEnvironmentClassifier(@NonNull objectName: String): Char {
        return getObjectNamePart(objectName, 0)[0]
    }

    @NonNull
    private fun getObjectType(@NonNull objectName: String): ObjectType {
        val objectTypeCode = getObjectNamePart(objectName, ObjectNamePart.OBJECT_TYPE_CODE.ordinal)
        return ObjectType.getByCode(objectTypeCode)
    }

    @NonNull
    private fun getObjectNamePart(@NonNull objectName: String, objectNamePart: Int): String {
        if (StringUtils.isEmpty(objectName)) {
            throw RuntimeException("Object name must not be empty.")
        }
        return objectName.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[objectNamePart]
    }

    private fun generateObjectName(objectType: ObjectType): String {
        return String.format("%c_%s_%s", ENVIRONMENT_CLASSIFIER, objectType.code, UUID.randomUUID().toString())
    }

    enum class ObjectType private constructor(
        @param:NonNull @field:NonNull
        @get:NonNull
        val code: String,
        @param:NonNull @field:NonNull
        @get:NonNull
        val mime: String
    ) {
        MAIN_DEFAULT_PROFILE_IMAGE("MDPI", MIME_JPG), CUSTOM_PROFILE_IMAGE("CPI", MIME_JPG),
        MAIN_DEFAULT_EVENT_IMAGE("MDEI", MIME_JPG), DEFAULT_EVENT_IMAGE("DEI", MIME_JPG), CUSTOM_EVENT_IMAGE(
            "CEI",
            MIME_JPG
        );

        companion object {

            init {
                checkCodesAreDistinct()
            }

            @NonNull
            fun getByCode(@NonNull code: String): ObjectType {
                for (objectType in values()) {
                    if (objectType.code == code) {
                        return objectType
                    }
                }
                throw RuntimeException("Unknown code.")
            }

            private fun checkCodesAreDistinct() {
                val objectTypes = values()
                if (objectTypes.size.toLong() != Stream.of(*objectTypes).map<String>({ it.code }).distinct().count()) {
                    throw RuntimeException("There are at least two object types with same codes.")
                }
            }
        }
    }

    private enum class ObjectNamePart {
        ENVIRONMENT_CLASSIFIER, OBJECT_TYPE_CODE, OBJECT_UUID
    }
}
