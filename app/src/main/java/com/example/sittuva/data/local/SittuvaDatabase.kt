package com.example.sittuva.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        PaymentSubscriptionEntity::class,
        PaymentEntity::class,
        PayoutEntity::class,
        BankAccountEntity::class,
        FeedbackEntity::class,
        ErrorReportEntity::class,
        NotificationEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SittuvaDatabase : RoomDatabase() {

    abstract fun sittuvaDao(): SittuvaDao

    companion object {
        @Volatile
        private var INSTANCE: SittuvaDatabase? = null

        fun getInstance(context: Context): SittuvaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SittuvaDatabase::class.java,
                    "sittuva_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
