package com.example.financeguide.data.remote

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketManager {
    private var socket: Socket? = null

    fun connect(serverUrl: String, token: String?, onEventReceived: (event: String, payload: JSONObject) -> Unit) {
        try {
            val opts = IO.Options().apply {
                query = "token=$token"
                forceNew = true
                reconnection = true
            }

            socket = IO.socket(serverUrl, opts).apply {
                on(Socket.EVENT_CONNECT) {
                    Log.d("SocketManager", "Connected to WebSocket Server")
                }

                on("expense:created") { args ->
                    if (args.isNotEmpty()) onEventReceived("expense:created", args[0] as JSONObject)
                }

                on("expense:deleted") { args ->
                    if (args.isNotEmpty()) onEventReceived("expense:deleted", args[0] as JSONObject)
                }

                on("group:updated") { args ->
                    if (args.isNotEmpty()) onEventReceived("group:updated", args[0] as JSONObject)
                }

                on("settlement:created") { args ->
                    if (args.isNotEmpty()) onEventReceived("settlement:created", args[0] as JSONObject)
                }

                connect()
            }
        } catch (e: Exception) {
            Log.e("SocketManager", "Failed to connect WebSocket", e)
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }
}
