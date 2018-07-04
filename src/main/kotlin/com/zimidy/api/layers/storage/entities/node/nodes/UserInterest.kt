package com.zimidy.api.layers.storage.entities.node.nodes

import com.zimidy.api.layers.storage.entities.node.Node
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import lombok.ToString
import java.util.stream.Collectors

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
data class UserInterest(

    var name: String

) : Node() {

    init {
        this.name = name.trim { it <= ' ' }.toLowerCase()
    }

    companion object {

        fun updateUserInterests(
            userInterests: MutableCollection<UserInterest>,
            userInterestNames: Collection<String>
        ) {
            val newUserInterests = userInterestNames.stream()
                .filter { userInterest -> !userInterest.isEmpty() }
                .map<UserInterest>({ UserInterest(it) })
                .collect(Collectors.toSet())
            // updateCollection(userInterests, newUserInterests)
        }
    }
}
