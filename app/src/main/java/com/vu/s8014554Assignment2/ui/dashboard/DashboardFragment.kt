package com.vu.s8014554Assignment2.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vu.s8014554Assignment2.R

/**
 * Displays the list of entities retrieved from the dashboard endpoint.
 * Tapping the details button navigates to the details screen.
 */
class DashboardFragment : Fragment() {

    private lateinit var detailsButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // RecyclerView and navigation
        findViewElements(view)
        setOnClickListeners()
    }

    private fun findViewElements(view: View) {
        detailsButton = view.findViewById(R.id.detailsButton)
    }

    private fun setOnClickListeners() {
        detailsButton.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_detailsFragment)
        }
    }
}