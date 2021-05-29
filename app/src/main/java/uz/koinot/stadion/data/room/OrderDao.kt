package uz.koinot.stadion.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.koinot.stadion.data.model.Order

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setAllOrder(list: List<Order>)

    @Query("select * from `Order` where stadiumId=:id")
    fun getAllOrders(id:Long):Flow<List<Order>>

    @Query("delete from `Order`")
    fun removeAllOrders()
}