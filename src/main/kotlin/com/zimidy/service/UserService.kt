package com.zimidy.service

import com.zimidy.api.layers.storage.entities.node.NodeId
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.repositories.node.nodes.UserRepository
import com.zimidy.exceptions.BusinessException
import lombok.NonNull
import org.neo4j.ogm.session.Session
import org.springframework.stereotype.Component

@Component
class UserService(
    val session: Session,
    val userRepository: UserRepository
) {

    @NonNull
    fun getUser(@NonNull userId: NodeId): User {
        val user = userRepository.get(userId)
        if (user == null) {
            throw BusinessException(BusinessException.MessageCode.USER_NOT_FOUND, userId)
        }
        return user
    }

    //    fun updateUserInfo(
//        user: User, connection: Connection, @Nullable email: String
//    ) {
//        val profile = connection.fetchUserProfile()
//        if (user.getEmail() == null) {
//            user.setEmail(email)
//        }
//        if (user.getFirstName() == null) {
//            user.setFirstName(profile.getFirstName())
//        }
//        if (user.getLastName() == null) {
//            user.setLastName(profile.getLastName())
//        }
//        if (user.getGender() == null) {
//            user.setGender(SocialUtils.determineGender(connection))
//        }
//    }
    companion object {
        const val MY_LOCATION = "My Location"
        const val SORT_TYPE_AGE = "age"
        const val SORT_TYPE_RATING = "rating"
        const val SORT_TYPE_DISTANCE = "distance"
        const val SORT_TYPE_FRIENDS = "friend-status"
        const val SORT_TYPE_GENDER = "gender"
    }
}
