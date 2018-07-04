package com.zimidy.api.layers.dto

import com.zimidy.api.layers.enumClass.LinkedAccount
import com.zimidy.api.layers.enumClass.PrivacyMode
import lombok.Getter
import lombok.Setter
import java.util.ArrayList

@Getter
@Setter
class UserSettingsDto {
    var accountsList = ArrayList<LinkedAccount>()
    var recentEventPrivacy: PrivacyMode? = null
    var upcomingEventPrivacy: PrivacyMode? = null
    var textEnabled: Boolean = false
}
