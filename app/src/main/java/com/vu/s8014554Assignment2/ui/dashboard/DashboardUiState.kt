package com.vu.s8014554Assignment2.ui.dashboard

import com.vu.s8014554Assignment2.data.model.Entity

/**
 * Every state the dashboard screen can be in.
 *
 * A sealed interface means the UI must handle each case, and the screen can
 * never be in two states at once. Unlike the login screen there is no Idle
 * state, because the dashboard starts fetching as soon as it opens.
 */
sealed interface DashboardUiState {

    /** Request in flight — show progress, hide the list. */
    data object Loading : DashboardUiState

    /** Entities loaded; [entities] is bound to the RecyclerView adapter. */
    data class Success(val entities: List<Entity>) : DashboardUiState

    /** Request failed; [message] is shown to the user. */
    data class Error(val message: String) : DashboardUiState
}