package com.heady.myunittests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heady.myunittests.ApiState.ERROR
import com.heady.myunittests.ApiState.SUCCESS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class) class VehicleViewModel(
    private val vehicleRepository: VehicleRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val SEARCH_TIME_OUT = 300L
        const val IGNORE_ITEM_COUNT = 1
        const val DEBOUNCE_TIME_OUT = 700L
        const val API_ERROR = "500 - Internal Server Error"
    }

    private val _viewState: MutableStateFlow<VehicleViewState> = MutableStateFlow(VehicleViewState())
    val viewState: Flow<VehicleViewState> = _viewState

    private val searchInputFlow: MutableStateFlow<String> = MutableStateFlow("")
    private val passwordInputFlow: MutableStateFlow<String> = MutableStateFlow("")

    private val _viewSearchState: MutableStateFlow<VehicleViewState> = MutableStateFlow(VehicleViewState())

    val viewSearchState: Flow<VehicleViewState> = _viewSearchState

    private val _password = MutableStateFlow<Pair<String, Boolean>>(Pair("", false))
    val password: Flow<Pair<String, Boolean>> = _password

    private var position: Int = 0
    private var category: String = ""

    init {
        position = savedStateHandle.get<Int>("POSITION") ?: 0
        category = savedStateHandle.get<String>("CATEGORY") ?: ""
        listenToInputs()
        fetchVehicleData()
        listenToSearchInput()
    }

    private fun listenToInputs() {
        viewModelScope.launch {
            passwordInputFlow
                .drop(IGNORE_ITEM_COUNT)
                .debounce(DEBOUNCE_TIME_OUT)
                .collect { it: String ->
                    _password.value = _password.value.copy(
                        first = it,
                        second = it.isNotBlank() &&
                            it.any { pass -> pass.isUpperCase() } &&
                            it.any { pass -> pass.isLowerCase() } &&
                            it.length >= 8
                    )
                }
        }
    }

    private fun fetchVehicleData() {
        viewModelScope.launch {
            val result = vehicleRepository.getVehicleData(position, category)

            result.collect { viewState: VehicleViewState ->
                when (viewState.state) {
                    SUCCESS -> {
                        _viewState.value = _viewState.value.copy(
                            engineList = viewState.engineList
                        )
                    }

                    ERROR -> {
                        _viewState.value = _viewState.value.copy(
                            error = API_ERROR
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun listenToSearchInput() {
        viewModelScope.launch {
            searchInputFlow
                .debounce(SEARCH_TIME_OUT)
                .filter { query -> query.isNotBlank() }
                .flatMapLatest { query ->
                    vehicleRepository.getVehicleData(position, category)
                }
                .collect { viewState: VehicleViewState ->
                    when (viewState.state) {
                        SUCCESS -> {
                            _viewSearchState.value = _viewSearchState.value.copy(
                                engineList = viewState.engineList
                            )
                        }

                        ERROR -> {
                            _viewSearchState.value = _viewSearchState.value.copy(
                                error = API_ERROR
                            )
                        }

                        else -> Unit
                    }
                }
        }
    }

    fun onSearchInputChanged(input: String) {
        searchInputFlow.value = input
    }

    fun setPassword(value: String) {
        passwordInputFlow.value = value
    }
}