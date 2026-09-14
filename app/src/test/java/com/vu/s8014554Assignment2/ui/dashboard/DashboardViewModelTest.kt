package com.vu.s8014554Assignment2.ui.dashboard


import com.vu.s8014554Assignment2.data.model.DashboardResponse
import com.vu.s8014554Assignment2.data.model.Entity
import com.vu.s8014554Assignment2.data.repository.DashboardRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response


/**
 * Unit tests for [DashboardViewModel].
 *
 * The repository is mocked with MockK so no network call is made, and the Main
 * dispatcher is replaced with a test dispatcher so viewModelScope coroutines
 * run under the test's control.
 *
 * Covers the success path, the 404 failure path, and the guard that prevents
 * a refetch when the entities are already loaded.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var repository: DashboardRepository

    private val monaLisa = Entity(
        artworkTitle = "Mona Lisa",
        artist = "Leonardo da Vinci",
        medium = "Oil paint",
        year = 1503,
        description = "A half-length portrait…"
    )
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        repository = mockk()
        Dispatchers.setMain(testDispatcher)
        viewModel = DashboardViewModel(repository)
    }

    @After
    fun tearDown() {
        // Restore the real Main dispatcher so other tests are unaffected.
        Dispatchers.resetMain()
    }

    @Test
    fun `loadEntities emits Success with the entity list`() = runTest(testDispatcher) {
        // Given
        val guernica = monaLisa.copy(artworkTitle = "Guernica", artist = "Pablo Picasso")
        coEvery { repository.getEntities("art") } returns
                DashboardResponse(listOf(monaLisa, guernica), 2)

        // When
        viewModel.loadEntities("art")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is DashboardUiState.Success)
        val entities = (state as DashboardUiState.Success).entities
        assertEquals(2, entities.size)
        assertEquals("Mona Lisa", entities[0].artworkTitle)
    }

    /**
     * The fragment calls loadEntities again after every configuration change, so
     * the ViewModel must reuse data it already holds instead of making a second
     * network request. This test fails if that check is removed.
     */
    @Test
    fun `loadEntities does not refetch once loaded`() = runTest(testDispatcher) {
        // Given a successful first load
        coEvery { repository.getEntities("art") } returns DashboardResponse(
            listOf(monaLisa), 1
        )
        viewModel.loadEntities("art")
        advanceUntilIdle()

        // When it is called again
        viewModel.loadEntities("art")
        advanceUntilIdle()

        // Then the repository was only hit once
        coVerify(exactly = 1) { repository.getEntities("art") }
    }

    @Test
    fun `a 404 emits an Error`() = runTest(testDispatcher) {
        // Given
        val http404 = HttpException(Response.error<Any>(404, "".toResponseBody(null)))
        coEvery { repository.getEntities("art") } throws http404

        // When
        viewModel.loadEntities("art")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is DashboardUiState.Error)
        assertTrue((state as DashboardUiState.Error).message.contains("Could not load artworks"))
    }

}

