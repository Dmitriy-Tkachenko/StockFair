package com.tkachenko.stockfair.utils

object Utils {
    const val URL_API = "https://financialmodelingprep.com/"
    const val URL_WSS = "wss://websockets.financialmodelingprep.com"
    private const val API_KEY = "f63392547466a1a657293049cca72f93"

    const val API_FETCH_STOCKS_ENDPOINT = "api/v3/quote/{symbol}?apikey=${API_KEY}"
    const val API_POPULAR_SYMBOLS_ENDPOINT = "api/v3/nasdaq_constituent?apikey=${API_KEY}"

    const val API_AUTH_WEBSOCKET_ENDPOINT = "{\"event\":\"login\",\"data\":{\"apiKey\":\"$API_KEY\"}}"

    const val PRICE_AND_PERCENT = "PRICE_AND_PERCENT"
    const val STAR = "STAR"
    const val VIEW_TYPE_STOCK = 1
    const val VIEW_TYPE_LOADING = 0

    val namesTabs = arrayOf(
        "Stocks",
        "Favourite"
    )

    fun subscribeToStocks(symbol: String) = "{\"event\":\"subscribe\",\"data\":{\"ticker\":\"${symbol.lowercase()}\"}}"
    fun unsubscribeToStocks(symbol: String) = "{\"event\":\"unsubscribe\",\"data\":{\"ticker\":\"${symbol.lowercase()}\"}}"

    fun loadImage(symbol: String) = "https://financialmodelingprep.com/image-stock/${symbol}.png"
}