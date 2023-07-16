package com.primapp.constants

object NotificationTypes {
    const val MENTORSHIP_REQUEST = "mentorship_request"
    const val MENTORSHIP_UPDATE = "mentorship_update"
    const val MENTORSHIP_REQUEST_ACTION = "mentorship_request_action"
    const val COMMUNITY_NOTIFICATION = "community_notification"
    const val POST_RELATED_NOTIFICATION = "post_notification"
    const val ADMIN_ACTION_NOTIFICATION = "admin_action"
    const val CONTRACT_NOTIFICATION = "contract_notification"
    const val POST_VIRUS_DETECTED_NOTIFICATION = "post_upload_notification"
}

object NotificationViewTypes {
    const val MENTORSHIP_REQUEST_VIEW = 2
    const val MENTORSHIP_UPDATE_VIEW = 3
    const val POST_NOTIFICATION_VIEW = 4

    //Local
    const val SEPARATOR_VIEW = 1
    const val OTHERS_VIEW = 0
}