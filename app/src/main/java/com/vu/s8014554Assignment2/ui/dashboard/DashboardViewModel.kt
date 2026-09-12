package com.vu.s8014554Assignment2.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vu.s8014554Assignment2.data.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * Holds the dashboard screen's state and fetches the entity list.
 *
 * The fragment calls [loadEntities] with the keypass it received from the
 * login screen, then observes [uiState]. Starts in
 * [DashboardUiState.Loading] because the screen fetches as soon as it opens.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository
) : ViewModel() {

    /** Backing state — only this ViewModel can change it. */
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)

    /** Read-only state observed by DashboardFragment. */
    val uiState: StateFlow<DashboardUiState> = _uiState

    /**
     * Fetches the entities for [keypass], unless they are already loaded.
     *
     * The fragment calls this from onViewCreated, which runs again after a
     * configuration change. Because the ViewModel survives rotation, the guard
     * below stops a redundant network request when the data is already held.
     *
     * @param keypass Topic key from the login response, e.g. "art".
     */
    fun loadEntities(keypass: String) {
        if (_uiState.value is DashboardUiState.Success) return

        _uiState.value = DashboardUiState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getEntities(keypass)
                _uiState.value = DashboardUiState.Success(response.entities)
            } catch (e: HttpException) {
                // Server replied, but not with 2xx. 404 = keypass is not a known topic.
                _uiState.value = DashboardUiState.Error(
                    when (e.code()) {
                        400, 401, 404 -> "Could not load artworks for this topic."
                        else -> "Server error (${e.code()}). Please try again."
                    }
                )
            } catch (e: SocketTimeoutException) {
                // Must come before IOException, which it extends.
                Log.w("DashboardViewModel", "Dashboard request timed out", e)
                _uiState.value =
                    DashboardUiState.Error("The server is waking up. Please try again in a moment.")
            } catch (e: IOException) {
                // Request never reached the server (offline, DNS failure, etc.).
                Log.w("DashboardViewModel", "Device is offline", e)
                _uiState.value =
                    DashboardUiState.Error("No internet connection. Check your network and try again.")
            } catch (e: Exception) {
                // Safety net for anything unexpected, e.g. a malformed JSON response.
                _uiState.value = DashboardUiState.Error("Something went wrong: ${e.message}")
            }
        }
    }
}