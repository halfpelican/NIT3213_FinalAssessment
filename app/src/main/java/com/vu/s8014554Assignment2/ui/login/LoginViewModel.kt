package com.vu.s8014554Assignment2.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vu.s8014554Assignment2.data.remote.Nit3213RetrofitClient
import com.vu.s8014554Assignment2.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

/**
 * Holds the login screen's state and performs the login request.
 *
 * The UI observes [uiState] and calls [login] when the user taps the button.
 * All network work runs in [viewModelScope], so it survives configuration
 * changes (e.g. rotation) and is cancelled when the ViewModel is cleared.
 */
class LoginViewModel : ViewModel() {
    // Created manually for now; will be injected by Hilt in the DI stage.
    private val repository = AuthRepository(Nit3213RetrofitClient().apiService)

    /** Backing state — only this ViewModel can change it. */
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    /** Read-only state observed by LoginFragment. */
    val uiState: StateFlow<LoginUiState> = _uiState

    /**
     * Validates the input, then sends the credentials to the API.
     *
     * Emits [LoginUiState.Loading] while the request is in flight, then either
     * [LoginUiState.Success] with the keypass or [LoginUiState.Error] with a
     * user-friendly message.
     *
     * @param username Student ID as typed; surrounding spaces are trimmed.
     * @param password Student first name; not trimmed because it is case-sensitive.
     */
    fun login(username: String, password: String) {
        val trimmedUsername = username.trim()
        // Avoid a pointless network call if either field is empty.
        if (trimmedUsername.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Please enter your student ID and first name.")
            return
        }
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                val response = repository.login(trimmedUsername, password)
                _uiState.value = LoginUiState.Success(response.keypass)
            } catch (e: HttpException) {
                // Server replied, but not with 2xx. 400 = wrong credentials (verified in Postman).
                _uiState.value = LoginUiState.Error(
                    when (e.code()) {
                        400, 401, 404 ->
                            "Incorrect student ID or first name (first name is case-sensitive)."

                        else -> "Server error (${e.code()}). Please try again."
                    }
                )
            } catch (e: SocketTimeoutException) {
                // Must come before IOException, which it extends.
                Log.w("LoginViewModel", "Login Timed out", e)
                _uiState.value =
                    LoginUiState.Error("The server is waking up. Please try again in a moment.")
            } catch (e: IOException) {
                // Request never reached the server (offline, DNS failure, etc.).
                Log.w("LoginViewModel", "Device is offline", e)
                _uiState.value =
                    LoginUiState.Error("No internet connection. Check your network and try again.")
            } catch (e: Exception) {
                // Safety net for anything unexpected, e.g. a malformed JSON response.
                _uiState.value = LoginUiState.Error("Something went wrong: ${e.message}")
            }
        }
    }

    /**
     * Resets the state to [LoginUiState.Idle] after the fragment has navigated.
     *
     * StateFlow replays its latest value to new collectors, so without this a
     * rotation after a successful login would trigger navigation a second time.
     */
    fun onNavigated() {
        _uiState.value = LoginUiState.Idle
    }
}