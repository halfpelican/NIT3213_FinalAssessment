package com.vu.s8014554Assignment2.ui.login

/**
 * Every state the login screen can be in.
 *
 * A sealed interface means the UI must handle each case, and the screen can
 * never be in two states at once (e.g. loading and showing an error).
 */
sealed interface LoginUiState {
    /** Nothing happening — initial state and after navigation. */
    data object Idle : LoginUiState

    /** Request in flight — show progress, disable the button. */
    data object Loading : LoginUiState

    /** Login succeeded; [keypass] is needed for the dashboard request. */
    data class Success(val keypass: String) : LoginUiState

    /** Login failed or input was invalid; [message] is shown to the user. */
    data class Error(val message: String) : LoginUiState
}