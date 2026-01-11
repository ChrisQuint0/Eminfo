// data/local/database/EmergencyInfoDatabase.kt
package com.eminfo.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eminfo.app.data.local.dao.*
import com.eminfo.app.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        EmergencyProfile::class,
        EmergencyContact::class,
        QRCodeSettings::class,
        EmergencyMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EmergencyInfoDatabase : RoomDatabase() {

    abstract fun emergencyProfileDao(): EmergencyProfileDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun qrCodeSettingsDao(): QRCodeSettingsDao
    abstract fun emergencyMessageDao(): EmergencyMessageDao

    companion object {
        @Volatile
        private var INSTANCE: EmergencyInfoDatabase? = null

        fun getDatabase(context: Context): EmergencyInfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EmergencyInfoDatabase::class.java,
                    "emergency_info_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        // Initialize default values
                        database.qrCodeSettingsDao().insertOrUpdate(QRCodeSettings())
                        database.emergencyMessageDao().insertOrUpdate(EmergencyMessage())
                    }
                }
            }
        }
    }
}