package com.vu.s8014554Assignment2.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.vu.s8014554Assignment2.R
import com.vu.s8014554Assignment2.ui.dashboard.DashboardFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Login screen: collects the student's ID and first name and authenticates
 * them against the nit3213api.
 *
 * Holds no logic of its own — it forwards the typed credentials to
 * [LoginViewModel] and renders whatever [LoginUiState] comes back. On 'success'
 * it passes the returned keypass to the Dashboard as a navigation argument.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var loginButton: Button

    /** Survives configuration changes, so a request in flight outlives a rotation. */
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var studentIdField: EditText
    private lateinit var passwordField: EditText
    private lateinit var errorText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewElements(view)
        setOnClickListeners()
        observeUiState()
    }

    /** Caches references to the views defined in fragment_login.xml. */
    private fun findViewElements(view: View) {
        loginButton = view.findViewById(R.id.loginButton)
        studentIdField = view.findViewById(R.id.studentIdField)
        passwordField = view.findViewById(R.id.passwordField)
        errorText = view.findViewById(R.id.errorText)
        progressBar = view.findViewById(R.id.progressBar)
    }

    /**
     * Sends the typed credentials to the ViewModel.
     *
     * No validation happens here; the ViewModel checks for blank fields and
     * trims the student ID before making the request.
     */
    private fun setOnClickListeners() {
        loginButton.setOnClickListener {
            viewModel.login(
                studentIdField.text.toString(),
                passwordField.text.toString()
            )
        }
    }

    /**
     * Observes [LoginViewModel.uiState] and re-renders on every change.
     *
     * Collection is tied to the view's lifecycle: repeatOnLifecycle(STARTED)
     * pauses it when the screen is not visible, and viewLifecycleOwner ensures
     * it stops when the fragment's view is destroyed, so no update ever targets
     * a view that no longer exists.
     */
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    /**
     * Applies one [LoginUiState] to the screen.
     *
     * The when is exhaustive because LoginUiState is sealed, so adding a new
     * state later will fail to compile until it is handled here.
     *
     * @param state The latest state emitted by the ViewModel.
     */
    private fun render(state: LoginUiState) {
        when (state) {
            LoginUiState.Idle -> {
                progressBar.isVisible = false
                loginButton.isEnabled = true
                errorText.isVisible = false
            }

            // Button is disabled while the request runs, to prevent double taps.
            LoginUiState.Loading -> {
                progressBar.isVisible = true
                loginButton.isEnabled = false
                errorText.isVisible = false
            }

            is LoginUiState.Error -> {
                progressBar.isVisible = false
                loginButton.isEnabled = true
                errorText.text = state.message
                errorText.isVisible = true
            }

            is LoginUiState.Success -> {
                progressBar.isVisible = false
                loginButton.isEnabled = true
                errorText.isVisible = false

                // The Dashboard needs the keypass for its GET /dashboard/{keypass} call.
                val args = Bundle().apply {
                    putString(DashboardFragment.ARG_KEYPASS, state.keypass)
                }
                findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment, args)

                // Reset to Idle so a rotation does not replay Success and navigate twice.
                viewModel.onNavigated()
            }
        }
    }
}