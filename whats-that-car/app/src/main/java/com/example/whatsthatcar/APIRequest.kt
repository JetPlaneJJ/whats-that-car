package com.example.whatsthatcar

import android.os.Build
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
class APIRequest {
    private var connection: HttpURLConnection? = null

    init {
        try {
            // TODO: replace with input instead of camry
            val url = URL("https://api.api-ninjas.com/v1/cars?model=camry")
            connection = url.openConnection() as HttpURLConnection
            connection!!.setRequestProperty("accept", "application/json")

            val responseStream = connection!!.inputStream
            val inputAsString = responseStream.bufferedReader().use { it.readText() }
            Log.i(DEBUG_TAG, inputAsString);

        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private const val DEBUG_TAG = "DEBUG_TAG"
    }
}