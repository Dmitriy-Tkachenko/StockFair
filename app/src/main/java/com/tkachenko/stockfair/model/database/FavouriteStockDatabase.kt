package com.tkachenko.stockfair.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tkachenko.stockfair.model.entities.FavouriteStock

@Database(entities = [FavouriteStock::class], version = 1)
abstract class FavouriteStockDatabase: RoomDatabase() {
    abstract fun favouriteStockDao(): FavouriteStockDao
}