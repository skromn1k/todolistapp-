package koreea_colea_sasha.data

import android.util.Log
import koreea_colea_sasha.domain.model.Address
import koreea_colea_sasha.domain.model.Location
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

object LocationDataSource {

    fun getLocation(lat: Double, long: Double, callback: (Address?) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$long")
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val gson = GsonBuilder().create()


                if (!body.isNullOrEmpty() && body.trim().startsWith("{")) {
                    try {
                        val result = gson.fromJson(body, Location::class.java)
                        // продолжай работу
                    } catch (e: Exception) {
                        Log.e("LocationDataSource", "Ошибка парсинга: ${e.message}")
                    }
                } else {
                    Log.e("LocationDataSource", "Некорректный ответ от сервера: $body")
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                println("failed to retrieve location")
                callback(null)
            }
        })
    }

}