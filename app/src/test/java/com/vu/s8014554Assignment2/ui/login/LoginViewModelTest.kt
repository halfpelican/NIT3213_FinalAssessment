package com.vu.s8014554Assignment2.ui.login

import com.vu.s8014554Assignment2.data.model.LoginResponse
import com.vu.s8014554Assignment2.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

/**
 * Unit tests for [LoginViewModel].
 *
 * The repository is mocked with MockK so no network call is made, and the Main
 * dispatcher is replaced with a test dispatcher so viewModelScope coroutines
 * run under the test's control.
 *
 * Covers input validation, a successful login, and the 400 credentials failure.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var repository: AuthRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        // Restore the real Main dispatcher so other tests are unaffected.
        Dispatchers.resetMain()
    }

    @Test
    fun `successful login emits Success with the keypass`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.login("8014554", "Benjamin") } returns LoginResponse("art")

        // When
        viewModel.login("8014554", "Benjamin")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is LoginUiState.Success)
        assertEquals("art", (state as LoginUiState.Success).keypass)
    }

    @Test fun`Blank fields produce Error without calling the repository`() = runTest(testDispatcher) {
        // When
        viewModel.login("", "")

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is LoginUiState.Error)
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test
    fun `An HttpException with code 400 produces the credentials Error message`() = runTest(testDispatcher) {
        // Given
        val http400 = HttpException(Response.error<Any>(400, "".toResponseBody(null)))
        coEvery { repository.login(any(), any()) } throws http400

        // When
        viewModel.login("8014554", "wrongname")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is LoginUiState.Error)
        assertTrue((state as LoginUiState.Error).message.contains("Incorrect student ID"))
    }
}

