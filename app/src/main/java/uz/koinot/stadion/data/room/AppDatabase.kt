package uz.koinot.stadion.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.koinot.stadion.data.model.Order

@Database(entities = [Order::class],version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun orderDao(): OrderDao
}