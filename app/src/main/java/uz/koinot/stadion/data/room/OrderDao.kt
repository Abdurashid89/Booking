package uz.koinot.stadion.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.koinot.stadion.data.model.Order

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setAllOrder(list: List<Order>)

    @Query("select * from `order`")
    fun getAllOrders():List<Order>

    @Query("delete from `order`")
    fun removeAllOrders()
}