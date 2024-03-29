package com.example.crs.RoyalApi

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder


class RoyalApiUtil {
    private val apiKey =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjM0MSwiaWRlbiI6IjMxOTU1NTA0ODMzMTgwNDY3MiIsIm1kIjp7fSwidHMiOjE1NTAwNzAzOTk0MjV9.XSdL-1eeDccp5bUsLayZgml1LBjqvxVel71AU3rxQZw"

    fun getPlayer(id: String): JSONObject? {
        return createRequest("https://api.royaleapi.com/player/$id")
    }

    fun searchPlayerByName(name: String): JSONObject? {
        return createRequest("https://royaleapi.com/data/player/search?q=${URLEncoder.encode(name)}")
    }

    private fun createRequest(url: String): JSONObject? {
        val request = Request(url)
        request.execute()
        try {
            return JSONObject(request.get())
        } catch (e: java.lang.Exception) {
            return null
        }
    }

    inner class Request internal constructor(private val url: String) : AsyncTask<Void, Void, String?>() {

        override fun doInBackground(vararg params: Void?): String? {
            val con = URL(url).openConnection()
            con.setRequestProperty("auth", apiKey)
            lateinit var result: StringBuffer
            try {
                val rider = con.getInputStream().bufferedReader()
                result = StringBuffer()
                do {
                    val line = rider.readLine()
                    if (line != null) {
                        result.append(line)
                    }
                } while (line != null)
            } catch (e: Exception) {
                Log.d("royalApi", "NIECO SA POKASLALO S APIROYAL " + e)
                return null
            }
            return result.toString()
        }
    }
}