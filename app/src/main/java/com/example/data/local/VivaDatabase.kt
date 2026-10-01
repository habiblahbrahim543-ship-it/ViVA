package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        User::class,
        Video::class,
        Comment::class,
        Message::class,
        Notification::class,
        Sound::class,
        Hashtag::class,
        ModerationReport::class,
        AppSetting::class,
        VerificationRequest::class,
        AdminNotification::class,
        BlockedUser::class,
        FollowRelation::class,
        SupportConversation::class,
        AdminAuditLog::class,
        LiveStream::class
    ],
    version = 3,
    exportSchema = false
)
abstract class VivaDatabase : RoomDatabase() {
    abstract fun vivaDao(): VivaDao

    companion object {
        @Volatile
        private var INSTANCE: VivaDatabase? = null

        fun getDatabase(context: Context): VivaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VivaDatabase::class.java,
                    "viva_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
