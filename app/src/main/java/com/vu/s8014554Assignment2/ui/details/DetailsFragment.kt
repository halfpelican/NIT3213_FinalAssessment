package com.vu.s8014554Assignment2.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.vu.s8014554Assignment2.R

/**
 * Details screen: shows everything about one artwork, including the
 * description that the dashboard list leaves out.
 *
 * Receives the artwork's fields as navigation arguments rather than fetching
 * anything, so it needs no ViewModel or repository. The keys are declared here
 * because this screen owns them; DashboardFragment refers to them when it
 * builds the bundle.
 */
class DetailsFragment : Fragment() {

    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var mediumYearText: TextView
    private lateinit var descriptionText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewElements(view)
        showArtwork()
    }

    /** Caches references to the views defined in fragment_details.xml. */
    private fun findViewElements(view: View) {
        titleText = view.findViewById(R.id.titleText)
        subtitleText = view.findViewById(R.id.subtitleText)
        mediumYearText = view.findViewById(R.id.mediumYearText)
        descriptionText = view.findViewById(R.id.descriptionText)
    }

    /**
     * Reads the artwork from the navigation arguments and binds it to the views.
     *
     * requireArguments fails fast if the screen was opened without arguments.
     * getInt returns 0 rather than null for a missing key, so [ARG_YEAR] needs
     * no null check.
     */
    private fun showArtwork() {
        val args = requireArguments()
        titleText.text = args.getString(ARG_TITLE)
        subtitleText.text = args.getString(ARG_ARTIST)
        mediumYearText.text = getString(
            R.string.entity_medium_year,
            args.getString(ARG_MEDIUM),
            args.getInt(ARG_YEAR)
        )
        descriptionText.text = args.getString(ARG_DESCRIPTION)
    }

    companion object {
        /** Keys for the artwork fields passed in from the dashboard screen. */
        const val ARG_TITLE = "artworkTitle"
        const val ARG_ARTIST = "artist"
        const val ARG_MEDIUM = "medium"
        const val ARG_YEAR = "year"
        const val ARG_DESCRIPTION = "description"
    }
}