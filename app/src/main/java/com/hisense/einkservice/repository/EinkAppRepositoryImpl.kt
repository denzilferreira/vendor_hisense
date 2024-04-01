package com.hisense.einkservice.repository

import com.hisense.einkservice.model.EinkApp
import kotlinx.coroutines.flow.Flow

class EinkAppRepositoryImpl(private val einkAppDao: EinkAppDao) : EinkAppRepository {
    override suspend fun insert(einkApp: EinkApp) {
        einkAppDao.insert(einkApp)
    }

    override suspend fun update(einkApp: EinkApp) {
        einkAppDao.update(einkApp)
    }

    override fun getAll(): Flow<List<EinkApp>> {
        return einkAppDao.getAll()
    }

    override fun getByPackageName(packageName: String): EinkApp? {
        return einkAppDao.getByPackageName(packageName)
    }
}