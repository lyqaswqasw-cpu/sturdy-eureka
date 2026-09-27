package com.example.data.util

import android.util.Log
import com.example.data.model.Channel
import com.example.data.model.ChannelServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object M3uParser {
    suspend fun parse(url: String, categoryName: String): List<Channel> = withContext(Dispatchers.IO) {
        val channels = mutableListOf<Channel>()
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val content = connection.inputStream.bufferedReader().use { it.readText() }
                val lines = content.lines()
                
                var currentName = ""
                var currentLogo = ""
                
                for (line in lines) {
                    val trimmed = line.trim()
                    if (trimmed.startsWith("#EXTINF:")) {
                        // Extract logo if present
                        val logoRegex = Regex("""tvg-logo="([^"]+)"""")
                        currentLogo = logoRegex.find(trimmed)?.groupValues?.get(1) ?: ""
                        
                        // Extract name (usually after the last comma)
                        val namePart = trimmed.substringAfterLast(",", "").trim()
                        if (namePart.isNotEmpty()) {
                            currentName = namePart
                        }
                    } else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                        // It's a URL line
                        if (currentName.isNotEmpty()) {
                            val stableId = "m3u_" + UUID.nameUUIDFromBytes("${url}_${categoryName}_${currentName}_${trimmed}".toByteArray()).toString()
                            channels.add(
                                Channel(
                                    id = stableId,
                                    name = currentName,
                                    imageUrl = currentLogo,
                                    category = categoryName,
                                    servers = listOf(
                                        ChannelServer(
                                            id = "srv_$stableId",
                                            name = "سيرفر رئيسي",
                                            streamUrl = trimmed,
                                            streamType = when {
                                                trimmed.endsWith(".m3u8", ignoreCase = true) -> "m3u8"
                                                trimmed.endsWith(".ts", ignoreCase = true) -> "ts"
                                                else -> "normal"
                                            }
                                        )
                                    )
                                )
                            )
                            currentName = ""
                            currentLogo = ""
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("M3uParser", "Error parsing M3U from $url: ${e.message}")
        }
        channels
    }
}
