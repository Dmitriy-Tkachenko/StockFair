package com.tkachenko.stockfair.model.network.websocket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class EchoWebSocketListener: WebSocketListener() {
    private val mLiveData = MutableLiveData<String>()
    val liveData: LiveData<String> get() = mLiveData

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        outputData(text)
    }

    private fun outputData(text: String) {
        mLiveData.postValue(text)
    }
}