package com.example.weathercontrol

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.net.URL
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    var weatherList: RecyclerView? = null
    var weatherArray: ArrayList<WeatherItem> = ArrayList()
    var location: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        location = getSystemService(LOCATION_SERVICE) as LocationManager
        weatherList = findViewById(R.id.weatherapp)
        var weatherAdapter = Adapt(weatherArray)
        var weatherLay = LinearLayoutManager(this)
        weatherList?.layoutManager = weatherLay
        weatherList?.adapter = weatherAdapter

        checkPermission()
    }
    private fun checkPermission(){
        if(ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 101)
        }
        else {
            requestLocation()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            101 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation()
                }
                else {
                    Toast.makeText(this, resources.getString(R.string.warning), Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
    private fun requestLocation() {
        try {
            location?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        }
        catch (e: SecurityException) {
            Toast.makeText(this, resources.getString(R.string.fail), Toast.LENGTH_LONG).show()
        }
    }
    private val locationListener: LocationListener = LocationListener { location -> getWeather(location.latitude.toString(), location.longitude.toString()) }

    private fun getWeather(latitude: String, longitude: String) {
        Thread {
            try {
                val weatherData = URL("https//api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&hourly=temperature_2m,weathercode")
                    .readText(Charsets.UTF_8)
                val hourly = JSONObject(weatherData).getJSONObject("hourly")
                for (i in 0 until hourly.getJSONArray("time").length()){
                    weatherArray.add(
                        WeatherItem(
                            hourly.getJSONArray("time").getString(i).split("T")[0],
                            hourly.getJSONArray("time").getString(i).split("T")[1],
                            weatherCodeToWeather(hourly.getJSONArray("weathercode").getInt(i)),
                            hourly.getJSONArray("temperature_2m").getDouble(i).roundToInt(),
                    )
                    )
                }
                runOnUiThread { weatherList?.adapter?.notifyDataSetChanged() }
            } catch (e: InterruptedException) {
                Log.e("TAB", e.message.toString())
            }
        }.start()
    }

    private fun weatherCodeToWeather(s: Int): String {
        val weather = when(code){
            0 -> "Ясно"
            in 1..2 -> "Переменная облачность"
            45 -> "Туман"
            48 -> "Изморось"
            in 51..55 -> "Морось"
            in 56..57 -> "Ледяная морось"
            in 61..65 -> "Дождь"
            in 71..77 -> "Снег"
            in 80..82 -> "Ливневый дождь"
            in 90..99 -> "Гроза"
            else -> "Облачно"
        }
        return weather
    }
}