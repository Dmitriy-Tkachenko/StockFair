package com.tkachenko.stockfair.model.entities

import com.google.gson.annotations.SerializedName

object Stocks {
    data class PopularSymbol (var symbol: String)

    data class StockItem (
        var logo: String,
        val name: String,
        val symbol: String,
        @SerializedName("price") var currentPrice: Double,
        @SerializedName("previousClose") var previousClosePrice: Double,
        var changePrice: Double,
        var changePercent: Double,
        var favouriteStock: Boolean = false)
}