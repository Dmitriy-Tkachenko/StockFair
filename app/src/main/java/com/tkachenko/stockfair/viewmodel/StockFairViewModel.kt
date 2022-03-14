package com.tkachenko.stockfair.viewmodel

import androidx.lifecycle.*
import com.tkachenko.stockfair.model.database.FavouriteStockRepository
import com.tkachenko.stockfair.model.entities.FavouriteStock
import com.tkachenko.stockfair.model.entities.Stocks
import com.tkachenko.stockfair.model.network.FMPFetch
import kotlinx.coroutines.launch

class StockFairViewModel: ViewModel() {
    private val favouriteStockRepository = FavouriteStockRepository.get()
    private lateinit var stocksWebSocketLiveData: LiveData<String> // Данные об акциях, которые получаем по вебсокету
    private lateinit var popularSymbols: List<Stocks.PopularSymbol> // Популярные символы акций по NASDAQ100
    private lateinit var stockItems: MutableList<Stocks.StockItem> // Данные акций
    private lateinit var symbolsUnsubscribe: List<String> // Акции, от которых нужно будет отписаться после прокрутки списка. (Огр: Макс. кол-во возможных подписок в api = 25)

    fun fetchStartStocksWebSocket(symbols: List<String>) = liveData {
        if (!::stocksWebSocketLiveData.isInitialized) {
            stocksWebSocketLiveData = FMPFetch().fetchStocksWebSocket(symbols)
            symbolsUnsubscribe = symbols
            emitSource(stocksWebSocketLiveData)
        }
    }

    fun fetchStocksWebSocket(symbols: List<String>) = liveData {
        stocksWebSocketLiveData = FMPFetch().fetchStocksWebSocket(symbolsUnsubscribe, symbols)
        symbolsUnsubscribe = symbols
        emitSource(stocksWebSocketLiveData)
    }

    fun fetchPopularSymbols() = liveData {
        if (!::popularSymbols.isInitialized) {
            popularSymbols = FMPFetch().fetchPopularSymbols()
            emit(popularSymbols)
        } else {
            emit(popularSymbols)
        }
    }

    fun fetchStartStocks(symbols: String) = liveData {
        if (!::stockItems.isInitialized) {
            stockItems = FMPFetch().fetchStocks(symbols = symbols).toMutableList()
        }
        emit(stockItems)
    }

    fun fetchStocks(symbols: String) = liveData {
        val stocks = FMPFetch().fetchStocks(symbols)
        stockItems.addAll(stocks)
        emit(stocks)
    }

    fun addFavouriteStock(favouriteStock: FavouriteStock) = viewModelScope.launch {
        favouriteStockRepository.addFavouriteStock(favouriteStock)
    }

    fun calcChangePrice(currentPrice: Double, previousClosePrice: Double) = currentPrice - previousClosePrice

    fun calcChangePercent(currentPrice: Double, previousClosePrice: Double) = currentPrice * 100 / previousClosePrice - 100
}