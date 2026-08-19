package com.goldmine.uncc.data.firebase

import android.content.Context
import android.util.Log
import com.goldmine.uncc.data.model.FreebieEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

/** Result of a live Firestore subscription. */
sealed interface FreebieFeed {
    data object Loading : FreebieFeed
    data class Success(val events: List<FreebieEvent>) : FreebieFeed
    data class Failure(val message: String) : FreebieFeed
    data object Unavailable : FreebieFeed
}

/**
 * Real-time repository for the shared freebie feed.
 *
 * Reads and writes the exact same `energyDrinkEvents` collection and document shape as the iOS
 * client, so reports posted from either platform appear on both, and the existing Cloud
 * Functions (`onEnergyDrinkEventUpdate` / `onEnergyDrinkEventEnded`) fire unchanged.
 */
class FreebieRepository(context: Context) {

    private val appContext = context.applicationContext
    private val available = FirebaseAvailability.isAvailable(appContext)
    private val firestore: FirebaseFirestore? =
        if (available) runCatching { FirebaseFirestore.getInstance() }.getOrNull() else null

    /** Live feed, filtered the same way the iOS service filters it. */
    fun observeEvents(): Flow<FreebieFeed> {
        val db = firestore ?: return flowOf(FreebieFeed.Unavailable)

        return callbackFlow {
            trySend(FreebieFeed.Loading)

            val registration = db.collection(COLLECTION)
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Freebie listener failed", error)
                        trySend(FreebieFeed.Failure(error.localizedMessage ?: "Could not load freebies"))
                        return@addSnapshotListener
                    }

                    val cutoff = twentyFourHoursAgo()
                    val events = snapshot?.documents
                        ?.mapNotNull(FreebieEvent::fromSnapshot)
                        ?.filter { !it.isEnded && it.date.after(cutoff) }
                        .orEmpty()

                    trySend(FreebieFeed.Success(events))
                }

            awaitClose { registration.remove() }
        }
    }

    suspend fun addEvent(event: FreebieEvent) {
        val db = firestore ?: error("Firebase is not configured")
        db.collection(COLLECTION).add(event.toMap()).await()
    }

    /**
     * Registers an up-vote. The write is done inside a transaction so simultaneous votes from
     * different devices cannot clobber each other — an improvement over the iOS
     * read-modify-write, which can silently drop votes.
     */
    suspend fun voteOnEvent(event: FreebieEvent, userName: String) {
        mutate(event) { current ->
            if (userName.isBlank() || userName in current.votedUserIds) return@mutate null
            current.copy(
                votes = current.votes + 1,
                votedUserIds = current.votedUserIds + userName,
            )
        }
    }

    /** Registers a "not available" vote; two of them end the event, as on iOS. */
    suspend fun noVoteOnEvent(event: FreebieEvent, userName: String) {
        mutate(event) { current ->
            if (userName.isBlank() || userName in current.noVotedUserIds) return@mutate null
            val noVotes = current.noVotes + 1
            current.copy(
                noVotes = noVotes,
                noVotedUserIds = current.noVotedUserIds + userName,
                isEnded = current.isEnded || noVotes >= FreebieEvent.VOTE_THRESHOLD,
            )
        }
    }

    suspend fun endEvent(event: FreebieEvent) {
        mutate(event) { current -> current.copy(isEnded = true) }
    }

    private suspend fun mutate(
        event: FreebieEvent,
        transform: (FreebieEvent) -> FreebieEvent?,
    ) {
        val db = firestore ?: error("Firebase is not configured")
        val documentId = event.documentId ?: error("Event is missing its Firestore document id")
        val ref = db.collection(COLLECTION).document(documentId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val current = FreebieEvent.fromSnapshot(snapshot) ?: return@runTransaction null
            val updated = transform(current) ?: return@runTransaction null
            transaction.set(ref, updated.toMap())
            null
        }.await()
    }

    private fun twentyFourHoursAgo(): Date = Calendar.getInstance().apply {
        add(Calendar.HOUR_OF_DAY, -24)
    }.time

    companion object {
        private const val TAG = "FreebieRepository"
        private const val COLLECTION = "energyDrinkEvents"
    }
}
