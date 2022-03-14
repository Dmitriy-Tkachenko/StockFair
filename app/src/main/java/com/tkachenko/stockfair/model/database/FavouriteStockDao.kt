package com.tkachenko.stockfair.model.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tkachenko.stockfair.model.entities.FavouriteStock

@Dao
interface FavouriteStockDao {
    @Query("SELECT * FROM favouritestock")
    fun getFavouriteStocks(): LiveData<List<FavouriteStock>>

    @Insert
    suspend fun addFavouriteStocks(favouriteStock: FavouriteStock)
}