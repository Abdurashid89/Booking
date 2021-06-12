package uz.koinot.stadion.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.Stadium

@Database(entities = [Order::class,Stadium::class],version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun orderDao(): OrderDao
}