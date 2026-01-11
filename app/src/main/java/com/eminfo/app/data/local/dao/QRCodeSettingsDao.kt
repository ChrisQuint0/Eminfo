package com.eminfo.app.data.local.dao

import androidx.room.*
import com.eminfo.app.data.local.entities.QRCodeSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface QRCodeSettingsDao {
    @Query("SELECT * FROM qr_settings WHERE id = 1")
    fun getSettings(): Flow<QRCodeSettings?>

    @Query("SELECT * FROM qr_settings WHERE id = 1")
    suspend fun getSettingsOnce(): QRCodeSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: QRCodeSettings)
}