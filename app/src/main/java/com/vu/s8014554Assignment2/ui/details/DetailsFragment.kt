package com.vu.s8014554Assignment2.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vu.s8014554Assignment2.R

/**
 * Displays the list of entities retrieved from the dashboard endpoint.
 * Tapping an entity navigates to the details screen.
 */
class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // RecyclerView and navigation wiring goes here
    }
}