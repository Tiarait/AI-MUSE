package ua.tiar.aim.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ua.tiar.aim.data.models.ImageResponseModel

@Database(entities = [ImageResponseModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageResponseDao(): ImageResponseDao
}