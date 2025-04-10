package com.example.weatherapijpcompose_tp9.viewmodel



import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapijpcompose_tp9.model.MeteoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
class MeteoViewModel(application: Application) : AndroidViewModel(application) {

    private val _meteoState = MutableStateFlow<MeteoItem?>(null)
    val meteoState: StateFlow<MeteoItem?> = _meteoState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun searchWeatherData(city: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val queue = Volley.newRequestQueue(getApplication())
            val url = "http://api.openweathermap.org/data/2.5/weather?q=$city&appid=e457293228d5e1465f30bcbe1aea456b"

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        Log.i("MyLog", "--------------------------------")
                        Log.i("MyLog", response)

                        val jsonObject = JSONObject(response)
                        val dateTimestamp = jsonObject.getLong("dt") * 1000
                        val date = Date(dateTimestamp)
                        val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy' T 'HH:mm")
                        val dateString = simpleDateFormat.format(date)

                        val main = jsonObject.getJSONObject("main")
                        val temp = (main.getDouble("temp") - 273.15).toInt()
                        val tempMin = (main.getDouble("temp_min") - 273.15).toInt()
                        val tempMax = (main.getDouble("temp_max") - 273.15).toInt()
                        val pression = main.getDouble("pressure").toInt()
                        val humidite = main.getDouble("humidity").toInt()

                        // Récupération de la vitesse du vent
                        val wind = jsonObject.getJSONObject("wind")
                        val vitesseVent = wind.getDouble("speed")

                        val weather = jsonObject.getJSONArray("weather")
                        val meteo = weather.getJSONObject(0).getString("main")

                        val meteoItem = MeteoItem(
                            temperature = temp,
                            tempMax = tempMax,
                            tempMin = tempMin,
                            pression = pression,
                            humidite = humidite,
                            image = meteo,
                            date = dateString,
                            ville = city,
                            vitesseVent = vitesseVent
                        )

                        _meteoState.value = meteoItem
                        _isLoading.value = false

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        _error.value = "Erreur de traitement des données"
                        _isLoading.value = false
                    }
                },
                { error ->
                    Log.i("MyLog", "-------Connection problem-------------------")
                    _error.value = "Ville non trouvée ou problème de connexion"
                    _isLoading.value = false
                }
            )

            queue.add(stringRequest)
        }
    }

    fun getWeatherIconResource(condition: String): Int {
        return when (condition) {
            "Rain" -> com.example.weatherapijpcompose_tp9.R.drawable.rainy
            "Clear" -> com.example.weatherapijpcompose_tp9.R.drawable.day
            "Thunderstorm" -> com.example.weatherapijpcompose_tp9.R.drawable.thunder
            "Clouds" -> com.example.weatherapijpcompose_tp9.R.drawable.cloudy
            else -> com.example.weatherapijpcompose_tp9.R.drawable.weather
        }
    }
}