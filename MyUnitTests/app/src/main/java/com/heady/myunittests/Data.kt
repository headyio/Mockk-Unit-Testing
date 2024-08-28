package com.heady.myunittests

import com.heady.myunittests.ApiState.INITIAL

data class EngineData(
    val id: Int,
    val model: String
)

data class VehicleData(
    val id: Int,
    val engineData: EngineData,
    val position: Int,
    val category: String
)

data class VehicleViewState(
    val engineList: List<VehicleData> = emptyList(),
    val state: ApiState = INITIAL,
    val error: String = ""
)

enum class ApiState {
    INITIAL,
    SUCCESS,
    ERROR
}