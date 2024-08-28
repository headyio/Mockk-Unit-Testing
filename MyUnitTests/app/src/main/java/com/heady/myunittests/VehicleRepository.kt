package com.heady.myunittests

import com.heady.myunittests.EngineData
import com.heady.myunittests.VehicleData
import com.heady.myunittests.VehicleViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class VehicleRepository {

    fun getVehicleData(
        position: Int,
        category: String
    ): Flow<VehicleViewState> {
        val list = mutableListOf<VehicleData>()
        return flow {
            repeat(5) { count ->
                val vehicleData = VehicleData(
                    id = count,
                    engineData = EngineData(id = count, model = "V$count"),
                    position = position,
                    category = category
                )
                list.add(vehicleData)
                delay(500)
            }
            emit(VehicleViewState(engineList = list))
        }.flowOn(Dispatchers.IO)
    }
}