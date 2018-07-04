package com.zimidy.api.layers.userImageSupport

import com.zimidy.api.AccessibleByAnyone
import com.zimidy.api.layers.storage.repositories.node.nodes.EventRepository
import com.zimidy.api.layers.storage.repositories.node.nodes.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(GcsController.URI)
class GcsController @Autowired
constructor(private val userRepository: UserRepository, private val eventRepository: EventRepository) {
    @RequestMapping(method = arrayOf(RequestMethod.GET), path = arrayOf(LIST_FILES))
    fun listObjects(@RequestParam("objectTypes[]") objectTypes: GcsUtils.ObjectType): List<String> {
        return GcsUtils.listObjectUrls(objectTypes)
    }

    @AccessibleByAnyone
    @RequestMapping(method = arrayOf(RequestMethod.POST), path = arrayOf(SET_USER_IMAGE))
    fun setUserImage(@RequestBody imagedto: ImageDto) {
        if (imagedto.id != "" && imagedto.id != null) {
            val user = userRepository.getOrThrow(imagedto.id)
            val testdata: String =
                GcsUtils.uploadDataAsObject(imagedto.imageUrl, GcsUtils.ObjectType.CUSTOM_PROFILE_IMAGE)
            user.defaultImageFileName = testdata
            userRepository.save(user)
        }
    }

    @AccessibleByAnyone
    @RequestMapping(method = arrayOf(RequestMethod.POST), path = arrayOf(SET_EVENT_IMAGE))
    fun setEventImage(@RequestBody imagedto: ImageDto) {
        val event = eventRepository.getOrThrow(imagedto.id)
        System.out.println("------------------------event Name- > " + event.name)
        val evenetPictureURL: String =
            (GcsUtils.uploadDataAsObject(imagedto.imageUrl, GcsUtils.ObjectType.CUSTOM_EVENT_IMAGE))
        System.out.println("-------------------------event image- > " + evenetPictureURL)
        event.pictureURL = evenetPictureURL
        System.out.println("-------------update event oimage ----------------- " + event.pictureURL)
        eventRepository.save(event)
    }

    companion object {
        const val URI = "/gcs"
        const val LIST_FILES = "/listFiles"
        const val SET_USER_IMAGE = "/set-user-image"
        const val SET_EVENT_IMAGE = "/set-event-image"
    }
}
