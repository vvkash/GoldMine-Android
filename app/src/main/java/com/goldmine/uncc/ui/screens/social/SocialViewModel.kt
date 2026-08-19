package com.goldmine.uncc.ui.screens.social

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.goldmine.uncc.data.firebase.FreebieFeed
import com.goldmine.uncc.data.firebase.FreebieRepository
import com.goldmine.uncc.data.firebase.NotificationHelper
import com.goldmine.uncc.data.model.FreebieEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Owns the live freebie feed plus transient UI errors. */
class SocialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FreebieRepository(application)

    val feed: StateFlow<FreebieFeed> = repository.observeEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FreebieFeed.Loading)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    fun dismissError() { _errorMessage.value = null }

    fun addEvent(event: FreebieEvent, onSuccess: () -> Unit) = viewModelScope.launch {
        _isSubmitting.value = true
        runCatching { repository.addEvent(event) }
            .onSuccess { onSuccess() }
            .onFailure { _errorMessage.value = it.localizedMessage ?: "Could not report the freebie" }
        _isSubmitting.value = false
    }

    fun vote(event: FreebieEvent, userName: String, notificationsEnabled: Boolean) =
        viewModelScope.launch {
            runCatching { repository.voteOnEvent(event, userName) }
                .onSuccess {
                    val crossedThreshold = event.votes + 1 >= FreebieEvent.VOTE_THRESHOLD &&
                        event.votes < FreebieEvent.VOTE_THRESHOLD && !event.hasNotifiedUsers
                    if (crossedThreshold && notificationsEnabled) {
                        NotificationHelper.showFreebieConfirmed(getApplication(), event)
                    }
                }
                .onFailure { _errorMessage.value = it.localizedMessage ?: "Could not record your vote" }
        }

    fun noVote(event: FreebieEvent, userName: String) = viewModelScope.launch {
        runCatching { repository.noVoteOnEvent(event, userName) }
            .onSuccess {
                val crossedThreshold = event.noVotes + 1 >= FreebieEvent.VOTE_THRESHOLD &&
                    event.noVotes < FreebieEvent.VOTE_THRESHOLD
                if (crossedThreshold) {
                    NotificationHelper.showFreebieEnded(getApplication(), event)
                }
            }
            .onFailure { _errorMessage.value = it.localizedMessage ?: "Could not record your vote" }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY],
                )
                return SocialViewModel(application) as T
            }
        }
    }
}
