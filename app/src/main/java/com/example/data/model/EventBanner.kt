package com.example.data.model

import java.util.UUID

data class EventBanner(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val imageUrl: String = "",
    val showWatchNow: Boolean = true,
    val watchNowUrl: String = "",
    val channelId: String = "",
    val channelName: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "imageUrl" to imageUrl,
        "showWatchNow" to showWatchNow,
        "watchNowUrl" to watchNowUrl,
        "channelId" to channelId,
        "channelName" to channelName,
        "createdAt" to createdAt,
        "appVersion" to "3.0.0"
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): EventBanner {
            return EventBanner(
                id = id,
                title = map["title"] as? String ?: "",
                imageUrl = map["imageUrl"] as? String ?: "",
                showWatchNow = map["showWatchNow"] as? Boolean ?: true,
                watchNowUrl = map["watchNowUrl"] as? String ?: "",
                channelId = map["channelId"] as? String ?: "",
                channelName = map["channelName"] as? String ?: "",
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}
