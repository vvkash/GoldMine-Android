package com.goldmine.uncc.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date
import java.util.UUID

/**
 * A crowd-sourced campus freebie ("energy drink event" in the original codebase).
 *
 * The Firestore representation is byte-for-byte compatible with what the iOS client writes via
 * its `Codable` conformance, so iOS and Android users share one live feed:
 *  - `id` is an uppercase UUID string
 *  - `date` is a Firestore `Timestamp`
 *  - `location` is a nested map of `id` / `title` / `latitude` / `longitude`
 */
data class FreebieEvent(
    val id: String = UUID.randomUUID().toString().uppercase(),
    val company: String,
    val location: MapLocation,
    val votes: Int,
    val date: Date,
    val noVotes: Int = 0,
    val isEnded: Boolean = false,
    val hasNotifiedUsers: Boolean = false,
    val votedUserIds: List<String> = emptyList(),
    val noVotedUserIds: List<String> = emptyList(),
    val documentId: String? = null,
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "company" to company,
        "location" to location.toMap(),
        "votes" to votes,
        "date" to Timestamp(date),
        "noVotes" to noVotes,
        "isEnded" to isEnded,
        "hasNotifiedUsers" to hasNotifiedUsers,
        "votedUserIds" to votedUserIds,
        "noVotedUserIds" to noVotedUserIds,
    )

    companion object {
        const val VOTE_THRESHOLD = 2

        fun fromSnapshot(doc: DocumentSnapshot): FreebieEvent? {
            val data = doc.data ?: return null
            val company = data["company"] as? String ?: return null
            val location = MapLocation.fromMap(data["location"] as? Map<*, *>) ?: return null
            val date = when (val raw = data["date"]) {
                is Timestamp -> raw.toDate()
                is Date -> raw
                is Number -> Date(raw.toLong())
                else -> return null
            }
            @Suppress("UNCHECKED_CAST")
            return FreebieEvent(
                id = data["id"] as? String ?: doc.id,
                company = company,
                location = location,
                votes = (data["votes"] as? Number)?.toInt() ?: 0,
                date = date,
                noVotes = (data["noVotes"] as? Number)?.toInt() ?: 0,
                isEnded = data["isEnded"] as? Boolean ?: false,
                hasNotifiedUsers = data["hasNotifiedUsers"] as? Boolean ?: false,
                votedUserIds = (data["votedUserIds"] as? List<String>) ?: emptyList(),
                noVotedUserIds = (data["noVotedUserIds"] as? List<String>) ?: emptyList(),
                documentId = doc.id,
            )
        }
    }
}
