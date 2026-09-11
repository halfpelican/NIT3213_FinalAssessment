package com.vu.s8014554Assignment2.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vu.s8014554Assignment2.R

/**
 *
 *
 */
class LoginFragment : Fragment() {

    private lateinit var loginButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // RecyclerView and navigation
        findViewElements(view)
        setOnClickListeners()
    }

    private fun findViewElements(view: View) {
        loginButton = view.findViewById(R.id.loginButton)
    }

    private fun setOnClickListeners() {
        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
        }
    }
}