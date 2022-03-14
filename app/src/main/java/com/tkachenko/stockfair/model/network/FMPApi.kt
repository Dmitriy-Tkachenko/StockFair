package com.tkachenko.stockfair.model.network

import com.tkachenko.stockfair.model.entities.Stocks
import com.tkachenko.stockfair.utils.Utils
import retrofit2.http.*

interface FMPApi {
    @GET(Utils.API_FETCH_STOCKS_ENDPOINT)
    suspend fun fetchStocks(@Path("symbol") symbol: String): List<Stocks.StockItem>

    @GET(Utils.API_POPULAR_SYMBOLS_ENDPOINT)
    suspend fun fetchPopularSymbols(): List<Stocks.PopularSymbol>
}