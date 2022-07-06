package com.udacity

object Constants {
    const val DURATION_ANIMATION_BUTTON : Long = 5000

    const val URL_GLIDE = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
    const val URL_RETROFIT = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    const val URL_LOAD_APP = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

    const val CHANNEL_ID = "channelId"
    const val NOTIFICATION_ID = 1997

    const val KEY_STATUS = "Status"
    const val KEY_FILE_NAME = "FileName"

    enum class Status {
        SUCCESS,
        FAIL
    }
}