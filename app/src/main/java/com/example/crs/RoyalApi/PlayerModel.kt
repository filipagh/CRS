package com.example.crs.RoyalApi

import android.graphics.Bitmap
import com.example.crs.MediaProjectionUtil
import info.debatty.java.stringsimilarity.Damerau
import org.json.JSONArray
import org.json.JSONObject

class PlayerModel(scrShot: Bitmap) {
    private val api = RoyalApiUtil()
    private var mainMediaProjectionUtil = MediaProjectionUtil()

    var playerData: JSONObject? = null

    init {
        parsePlayer(scrShot)
    }

    private fun parsePlayer(scrShot: Bitmap): Boolean {
        val parsedBlocks = mainMediaProjectionUtil.parseTextFromBitmap(scrShot)
        if (getPlayer(parsedBlocks[1])) {
            return true
        }

        val id = parsedBlocks[1].substring(1)
        if (getPlayer(id)) {
            return true
        }

        val playerApiId = searchPlayer(parsedBlocks[0], parsedBlocks[1])
        // TODO vyziadat pouzivatela o potvrdenie / opravenie ID ktore sme nacitali cez royalapi SEARCH
        if (playerApiId != null) {
            if (getPlayer(playerApiId)) {
                return true
            }
        }
        // todo vyziadat pouzivatela aby opravil ID lebo sme nenasli zhodu
        return false
    }

    private fun searchPlayer(name: String, id: String): String? {
        val a = api.searchPlayerByName(name)
        val d = Damerau()
        var min = 999
        var tag: String? = null

        for (i in 0 until (a!!.get("results") as JSONArray).length()) {
            val item = (a.get("results") as JSONArray).getJSONObject(i)
            val apiId = (item as JSONObject).get("player_tag") as String?
            val ocrId = id.toUpperCase()

            val distance = d.distance(apiId, ocrId)
            if (distance < min) {
                min = distance.toInt()
                tag = item.get("player_tag") as String?
            }
        }
        return tag
    }

    private fun getPlayer(id: String): Boolean {
        playerData = api.getPlayer(id)
        return playerData != null
    }
}

