package com.hisense.einkservice.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hisense.einkservice.model.EinkApp
import kotlinx.coroutines.flow.Flow

@Dao
interface EinkAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(einkApp: EinkApp)

    @Update
    suspend fun update(einkApp: EinkApp)

    @Query("SELECT * FROM eink_apps")
    fun getAll(): Flow<List<EinkApp>>

    @Query("SELECT * FROM eink_apps WHERE packageName = :packageName")
    fun getByPackageName(packageName: String): EinkApp?
}
