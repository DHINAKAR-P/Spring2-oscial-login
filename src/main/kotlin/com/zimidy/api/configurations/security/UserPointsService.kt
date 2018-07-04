package com.zimidy.api.configurations.security

import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.repositories.node.nodes.UserRepository
import org.springframework.stereotype.Service

@Service
class UserPointsService(
    private val userRepository: UserRepository
) {

    fun addSignUpPoints(user: User) {
        addBalancePoints(user, POINTS_FOR_SIGN_UP) // userPoints saved
    }

    private fun addBalancePoints(user: User, points: Long?) {
        var points = points
        val balance = user.pointBalance
        if (points == null || points.equals(0)) {
            points = 0L
        }
        var newPoints = balance!!.plus(points)
        if (newPoints < 0) {
            newPoints = 0
        }
        user.pointBalance = newPoints
        userRepository.save(user)
    }

    companion object {
        const val POINTS_FOR_SIGN_UP: Long = 50
        const val POINTS_FOR_CREATE_EVENT_FIRST_TIME: Long = 35
        const val POINTS_FOR_CREATE_EVENT_FROM_DFA_CHAT: Long = 20
        const val POINTS_FOR_INVITE_FRIEND: Long = 12
        const val POINTS_FOR_NEW_ATTENDEE_0: Long = 30
        const val POINTS_FOR_NEW_ATTENDEE_1: Long = 15
        const val POINTS_FOR_NEW_ATTENDEE_2: Long = 7
        const val POINTS_FOR_NEW_ATTENDEE_3: Long = 0
        const val POINTS_FOR_APPLY_EVENT: Long = 30
    }
}
