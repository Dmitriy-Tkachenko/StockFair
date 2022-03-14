package com.tkachenko.stockfair.model.network.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object WebSocketClient {
    private var webSocket: WebSocket? = null
    private var listener: WebSocketListener = EchoWebSocketListener()

    fun getWebSocket(url: String): WebSocket {
        if (webSocket == null) {
            val httpClient = OkHttpClient()
            val request: Request = Request.Builder()
                .url(url)
                .build()
            webSocket = httpClient.newWebSocket(request, listener)
        }
        return webSocket!!
    }

    fun getWebSocketListener(): EchoWebSocketListener {
        return listener as EchoWebSocketListener
    }
}