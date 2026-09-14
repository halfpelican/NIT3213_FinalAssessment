package com.vu.s8014554Assignment2.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.vu.s8014554Assignment2.R
import com.vu.s8014554Assignment2.data.model.Entity
import com.vu.s8014554Assignment2.ui.details.DetailsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Dashboard screen: lists the artworks returned by the nit3213api.
 *
 * Receives the keypass produced by the login request as a navigation argument
 * and uses it for the GET /dashboard/{keypass} call. Entities are shown in a
 * RecyclerView; tapping one opens the details screen with that artwork.
 */
@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var entityList: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var adapter: EntityAdapter

    /** Topic key from the login response; identifies which entity set to fetch. */
    private lateinit var keypass: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewElements(view)

        // Fails fast if the screen was opened without a keypass, rather than
        // surfacing later as a confusing API error.
        keypass = requireNotNull(requireArguments().getString(ARG_KEYPASS)) {
            "DashboardFragment requires a keypass argument"
        }

        setupRecyclerView()
        observeUiState()
        viewModel.loadEntities(keypass)
    }

    /** Caches references to the views defined in fragment_dashboard.xml. */
    private fun findViewElements(view: View) {
        entityList = view.findViewById(R.id.entityList)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)
    }

    /**
     * Attaches the adapter, passing it the click behaviour.
     *
     * The layout manager is declared in XML, so it is not set here.
     */
    private fun setupRecyclerView() {
        adapter = EntityAdapter { entity -> openDetails(entity) }
        entityList.adapter = adapter
    }

    /**
     * Opens the details screen for [entity].
     *
     * The entity's fields travel individually in the arguments bundle, which
     * avoids making the model parcelable for a five-field object.
     */
    private fun openDetails(entity: Entity) {
        val args = Bundle().apply {
            putString(DetailsFragment.ARG_TITLE, entity.artworkTitle)
            putString(DetailsFragment.ARG_ARTIST, entity.artist)
            putString(DetailsFragment.ARG_MEDIUM, entity.medium)
            putInt(DetailsFragment.ARG_YEAR, entity.year)
            putString(DetailsFragment.ARG_DESCRIPTION, entity.description)
        }
        findNavController().navigate(R.id.action_dashboardFragment_to_detailsFragment, args)
    }

    /** Observes [DashboardViewModel.uiState] and re-renders on every change. */
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    /**
     * Applies one [DashboardUiState] to the screen.
     *
     * Note that Success only fills the list — navigation to the details screen
     * is driven by row taps, not by a state change.
     */
    private fun render(state: DashboardUiState) {
        when (state) {
            DashboardUiState.Loading -> {
                progressBar.isVisible = true
                entityList.isVisible = false
                errorText.isVisible = false
            }

            is DashboardUiState.Success -> {
                progressBar.isVisible = false
                adapter.updateData(state.entities)
                entityList.isVisible = true
                errorText.isVisible = false
            }

            is DashboardUiState.Error -> {
                progressBar.isVisible = false
                entityList.isVisible = false
                errorText.text = state.message
                errorText.isVisible = true
            }
        }
    }

    companion object {
        /** Key for the keypass passed in from the login screen. */
        const val ARG_KEYPASS = "keypass"
    }
}