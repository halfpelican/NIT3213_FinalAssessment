package com.vu.s8014554Assignment2.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vu.s8014554Assignment2.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Dashboard screen: lists the entities returned by the nit3213api.
 *
 * Receives the keypass produced by the login request as a navigation argument
 * and uses it for the GET /dashboard/{keypass} call. Entities will be shown in
 * a RecyclerView, and tapping one opens the details screen.
 */
@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private lateinit var detailsButton: Button

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
        Log.d("Dashboard", "Received keypass: $keypass")

        setOnClickListeners()
    }

    /** Caches references to the views defined in fragment_dashboard.xml. */
    private fun findViewElements(view: View) {
        detailsButton = view.findViewById(R.id.detailsButton)
    }

    /** Placeholder navigation; to be replaced by RecyclerView item clicks. */
    private fun setOnClickListeners() {
        detailsButton.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_detailsFragment)
        }
    }

    companion object {
        /** Key for the keypass passed in from the login screen. */
        const val ARG_KEYPASS = "keypass"
    }
}