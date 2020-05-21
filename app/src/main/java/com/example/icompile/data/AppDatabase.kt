package me.marthia.icompile.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.marthia.icompile.auth.User
import me.marthia.icompile.auth.UserDao


@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "users"

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                    context, AppDatabase::class.java,
                    DATABASE_NAME
                )
                .addCallback(object : RoomDatabase.Callback() {
                })
                .build()
        }
    }
}