package com.tkachenko.stockfair.model.network

import androidx.lifecycle.LiveData
import com.tkachenko.stockfair.model.network.websocket.WebSocketClient
import com.tkachenko.stockfair.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FMPFetch {
    private val mboumApi: FMPApi get() = RetrofitClient.getClient(url = Utils.URL_API).create(FMPApi::class.java)
    private val webSocket get() = WebSocketClient.getWebSocket(url = Utils.URL_WSS)
    private val listener get() = WebSocketClient.getWebSocketListener()

    private fun authWebSocket() {
        webSocket.send(Utils.API_AUTH_WEBSOCKET_ENDPOINT)
    }

    private fun subscribeToStocks(symbols: List<String>) {
        for (symbol in symbols) {
            webSocket.send(Utils.subscribeToStocks(symbol = symbol))
        }
    }

    private fun unsubscribeFromStocks(symbols: List<String>) {
        for (symbol in symbols) {
            webSocket.send(Utils.unsubscribeToStocks(symbol = symbol))
        }
    }

    suspend fun fetchStocksWebSocket(symbols: List<String>): LiveData<String> {
        withContext(Dispatchers.IO) {
            authWebSocket()
            subscribeToStocks(symbols)
        }

        return listener.liveData
    }

    suspend fun fetchStocksWebSocket(symbolsUnsubscribe: List<String>, symbolsSubscribe: List<String>): LiveData<String> {
        withContext(Dispatchers.IO) {
            unsubscribeFromStocks(symbolsUnsubscribe)
            subscribeToStocks(symbolsSubscribe)
        }
        return listener.liveData
    }

    suspend fun fetchPopularSymbols() = withContext(Dispatchers.IO) { mboumApi.fetchPopularSymbols() }

    suspend fun fetchStocks(symbols: String) = withContext(Dispatchers.IO) { mboumApi.fetchStocks(symbols) }
}