package com.tkachenko.stockfair.model.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.tkachenko.stockfair.model.entities.FavouriteStock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import java.util.concurrent.Executors

class FavouriteStockRepository private constructor(context: Context) {
    private val database: FavouriteStockDatabase = Room.databaseBuilder(
        context.applicationContext,
        FavouriteStockDatabase::class.java,
        "favourite-stock-database"
    ).build()

    private val favouriteStockDatabase = database.favouriteStockDao()

    fun getFavouriteStocks(): LiveData<List<FavouriteStock>> = favouriteStockDatabase.getFavouriteStocks()

    suspend fun addFavouriteStock(favouriteStock: FavouriteStock) {
        withContext(Dispatchers.IO) {
            favouriteStockDatabase.addFavouriteStocks(favouriteStock)
        }
    }

    companion object {
        private var INSTANCE: FavouriteStockRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) INSTANCE = FavouriteStockRepository(context)
        }

        fun get(): FavouriteStockRepository {
            return INSTANCE ?: throw IllegalStateException("FavouriteStockRepository must be initialized")
        }
    }
}