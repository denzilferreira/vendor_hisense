package com.hisense.einkservice.repository

import com.hisense.einkservice.model.EinkApp
import kotlinx.coroutines.flow.Flow

interface EinkAppRepository {
    suspend fun insert(einkApp: EinkApp)

    suspend fun update(einkApp: EinkApp)

    fun getAll(): Flow<List<EinkApp>>

    fun getByPackageName(packageName: String): EinkApp?
}
