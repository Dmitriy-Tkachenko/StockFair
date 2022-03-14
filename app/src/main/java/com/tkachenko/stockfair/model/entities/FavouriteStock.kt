package com.tkachenko.stockfair.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavouriteStock(
    @PrimaryKey val symbol: String,
    val name: String,
    val currentPrice: Double,
    val changePrice: Double,
    val changePercent: Double)