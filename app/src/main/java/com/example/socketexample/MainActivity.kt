package com.example.socketexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.socketexample.model.Currency
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString

class MainActivity : AppCompatActivity() {
    var mWebSocket: WebSocket? = null
    lateinit var tv_socket: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews(){
        tv_socket = findViewById(R.id.tv_socket)
        connectToSocket()
    }

    private fun connectToSocket() {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url("wss://ws.bitstamp.net").build()
        client.newWebSocket(request, object: WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                mWebSocket = webSocket
                webSocket.send("{\n" +
                        "    \"event\": \"bts:subscribe\",\n" +
                        "    \"data\": {\n" +
                        "        \"channel\": \"live_trades_btcusd\"\n" +
                        "    }\n" +
                        "}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("@@@", "Receiving: $text")
                runOnUiThread {
                    val data = Gson().fromJson(text, Currency::class.java)
                    tv_socket.text = "1 BTC = ${data.data.price_str} $"
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("@@@", "Receiving bytes: $bytes")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("@@@", "Closing: $code / $reason")
                // webSocket.close(1000, null)
                //webSocket.cancel()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d("@@@", "Error: ${t.message}")
            }
        })
        client.dispatcher.executorService.shutdown()
    }
}