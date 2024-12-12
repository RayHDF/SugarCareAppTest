package com.rayhdf.sugarcareapp.ui.home.track

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rayhdf.sugarcareapp.data.model.TrackingItem
import com.rayhdf.sugarcareapp.data.preferences.UserPreference
import com.rayhdf.sugarcareapp.data.preferences.dataStore
import com.rayhdf.sugarcareapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime


class TrackViewModel(context: Context) : ViewModel() {

    private val userRepository = UserRepository()
    private val userPreference = UserPreference.getInstance(context.dataStore)

    private val _recentTracks = MutableStateFlow<List<TrackingItem>>(emptyList())
    val recentTracks: StateFlow<List<TrackingItem>> = _recentTracks

    private val _tracks = MutableStateFlow<List<TrackingItem>>(emptyList())
    val tracks : StateFlow<List<TrackingItem>> = _tracks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var showDialog by mutableStateOf(false)
    var sugarIntake by mutableStateOf("")
    var weight by mutableStateOf("")

    fun track(onResult: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userPreference.getSession().first()
                val userId = user.userId
                if (userId.isNotEmpty()) {
                    val response = withContext(Dispatchers.IO) {
                        userRepository.track(userId, sugarIntake.toFloat(), weight.toFloat())
                    }
                    if (response.message == "Track stored successfully") {
                        Log.d("Tracking Post", "$response")
                        onSuccess()
                    } else {
                        onResult("Track Failed: ${response.message}")
                    }
                } else {
                    onResult("Track Failed: User ID empty")
                }
            } catch (e: Exception) {
                onResult("Track failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getTracks() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = userPreference.getSession().first()
                val userId = user.userId
                Log.d("Get Tracks", "id: $userId")
                if (userId.isNotEmpty()) {
                    val response = withContext(Dispatchers.IO) {
                        userRepository.getTracks(userId)
                    }
                    val sortedTracks = response.tracking
                        ?.filterNotNull()
                        ?.sortedByDescending { it.data?.createdAt?.let { date -> ZonedDateTime.parse(date) } }
                        ?: emptyList()
                    _tracks.value = sortedTracks
                    _recentTracks.value = sortedTracks.take(5).reversed()
                    Log.d("Get Tracks", "$response")
                } else {
                    Log.d("Get Tracks", "User ID is empty")
                }
            } catch (e: Exception) {
                Log.d("Get Tracks", "Error: $e")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getRecommendations() {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {

            }
        }
    }

}