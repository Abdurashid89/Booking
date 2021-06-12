package uz.koinot.stadion.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.Stadium

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setAllOrder(list: List<Order>)

    @Query("select * from `Order` where stadiumId=:id order by id desc")
    fun getAllOrders(id:Long):Flow<List<Order>>

    @Query("delete from `Order`")
    fun removeAllOrders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAllStadium(list: List<Stadium>)

    @Query("select * from `Stadium`")
    fun getAllStadiums(): Flow<List<Stadium>>

    @Query("delete from `Stadium`")
    suspend fun removeAllStadiums()
}