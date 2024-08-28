package com.heady.myunittests

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.heady.myunittests.VehicleViewModel.Companion.API_ERROR
import com.heady.myunittests.VehicleViewModel.Companion.DEBOUNCE_TIME_OUT
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineRule()

    @get:Rule
    val testName = TestName()

    @MockK
    private lateinit var vehicleRepository: VehicleRepository

    private lateinit var viewModel: VehicleViewModel

    private val engineList = listOf(1, 2, 3, 4, 5).map { it: Int ->
        VehicleData(
            id = it,
            engineData = EngineData(id = it, model = "V$it"),
            position = 0,
            category = "Random"
        )
    }

    private var savedStateHandle = SavedStateHandle().apply {
        set("POSITION", 1)
        set("CATEGORY", "Random")
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        when (testName.methodName) {
            "Check api call is successful and gives list",
            "Check on non blank search input you get search results",
            "Check on blank search input api will not get called",
            "On clearSearchResults verify empty state",
            "Check password input is valid or not" -> {
                coEvery { vehicleRepository.getVehicleData(1, "Random") } coAnswers {
                    flow {
                        emit(VehicleViewState(engineList = engineList, state = ApiState.SUCCESS))
                    }
                }
            }

            "Check api call failed and gives error message",
            "Check search input failed to get results" -> {
                coEvery { vehicleRepository.getVehicleData(1, "Random") } coAnswers {
                    flow {
                        emit(VehicleViewState(error = API_ERROR, state = ApiState.ERROR))
                    }
                }
            }
        }

        viewModel = VehicleViewModel(
            vehicleRepository = vehicleRepository,
            savedStateHandle = savedStateHandle
        )
    }

    @Test
    fun `Check api call is successful and gives list`() = runTest {
        viewModel.viewState.test {
            val result = awaitItem()
            val expected = VehicleViewState(engineList = engineList)
            Assert.assertEquals(expected, result)
            coVerify(exactly = 1) {
                vehicleRepository.getVehicleData(1, "Random")
            }
            confirmVerified(vehicleRepository)
        }
    }

    @Test
    fun `Check api call failed and gives error message`() = runTest {
        viewModel.viewState.test {
            val result = awaitItem()
            val expected = VehicleViewState(error = API_ERROR)
            Assert.assertEquals(expected, result)
            coVerify(exactly = 1) {
                vehicleRepository.getVehicleData(1, "Random")
            }
            confirmVerified(vehicleRepository)
        }
    }

    @Test
    fun `Check on non blank search input you get search results`() = runTest {
        viewModel.viewSearchState.test {
            viewModel.onSearchInputChanged("query")
            skipItems(1)
            val result = awaitItem()
            val expected = VehicleViewState(engineList = engineList)
            Assert.assertEquals(expected, result)
            coVerify(exactly = 2) {
                vehicleRepository.getVehicleData(1, "Random")
            }
            confirmVerified(vehicleRepository)
        }
    }

    @Test
    fun `Check search input failed to get results`() = runTest {
        viewModel.viewSearchState.test {
            viewModel.onSearchInputChanged("query")
            skipItems(1)
            val result = awaitItem()
            val expected = VehicleViewState(error = API_ERROR)
            Assert.assertEquals(expected, result)
            coVerify(exactly = 2) {
                vehicleRepository.getVehicleData(1, "Random")
            }
            confirmVerified(vehicleRepository)
        }
    }

    @Test
    fun `Check on blank search input api will not get called`() = runTest {
        viewModel.viewSearchState.test {
            viewModel.onSearchInputChanged("")
            val result = awaitItem()
            val expected = VehicleViewState()
            Assert.assertEquals(expected, result)
            coVerify(exactly = 1) {
                vehicleRepository.getVehicleData(1, "Random")
            }
            confirmVerified(vehicleRepository)
        }
    }

    @Test
    fun `Check password input is valid or not`() = runTest {
        viewModel.password.test {
            println(awaitItem())

            advanceTimeBy(DEBOUNCE_TIME_OUT)
            viewModel.setPassword("Test")
            var passwordValidationState = awaitItem()
            Assert.assertEquals(false, passwordValidationState.second)

            advanceTimeBy(DEBOUNCE_TIME_OUT)
            viewModel.setPassword("Test@12")
            passwordValidationState = awaitItem()
            Assert.assertEquals(false, passwordValidationState.second)

            advanceTimeBy(DEBOUNCE_TIME_OUT)
            viewModel.setPassword("Test@123")
            passwordValidationState = awaitItem()
            Assert.assertEquals(true, passwordValidationState.second)

            advanceTimeBy(DEBOUNCE_TIME_OUT)
            viewModel.setPassword("")
            passwordValidationState = awaitItem()
            Assert.assertEquals(false, passwordValidationState.second)

            advanceTimeBy(DEBOUNCE_TIME_OUT)
            viewModel.setPassword("Tes!@#\$%&*333")
            passwordValidationState = awaitItem()
            Assert.assertEquals(true, passwordValidationState.second)

            advanceTimeBy(DEBOUNCE_TIME_OUT)
            viewModel.setPassword("Tes-3388")
            passwordValidationState = awaitItem()
            Assert.assertEquals(true, passwordValidationState.second)
        }
    }
}