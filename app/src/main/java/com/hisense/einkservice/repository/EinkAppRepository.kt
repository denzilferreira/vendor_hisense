package com.hisense.einkservice.repository

import com.hisense.einkservice.model.EinkApp
import kotlinx.coroutines.flow.Flow

interface EinkAppRepository {
    suspend fun insert(einkApp: EinkApp)

    suspend fun update(einkApp: EinkApp)

    suspend fun delete(einkApp: EinkApp)

    fun getAll(): Flow<List<EinkApp>>

    suspend fun getByPackageName(packageName: String): EinkApp?
}
