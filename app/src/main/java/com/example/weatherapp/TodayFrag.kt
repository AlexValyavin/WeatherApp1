package com.example.weatherapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.FragmentTodayBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API = "8f55e87ff1e7424696075013221910"
const val PREF = "Table"
const val KEY = "key"

class TodayFrag : Fragment() {
    private lateinit var binding: FragmentTodayBinding
    private val model: MainViewModel by activityViewModels()
    private lateinit var adapter: RvAdapter
    private var city:String = ""
    private lateinit var pref : SharedPreferences
    lateinit var mainObj:JSONObject
    private lateinit var pLauncher: ActivityResultLauncher<String>
    var longitude:Float = 0.0F
    var latitude:Float = 0.0F

    val eng = listOf(
        "0",
        "1",
        "2",
        "3",
        "45",
        "48",
        "51",
        "53",
        "55",
        "56",
        "57",
        "61",
        "63",
        "65",
        "66",
        "67",
        "71",
        "73",
        "75",
        "77",
        "80",
        "81",
        "82",
        "85",
        "86",
        "95",
        "96",
        "99")
    val rus = listOf(
        "Ясно",
        "Преимущественно ясно",
        "Переменная облачность",
        "Пасмурно",
        "Туман",
        "Морозный туман",
        "Легкий дождь",
        "Умеренный дождь",
        "Сильный дождь",
        "Моросящий дождь",
        "Моросящий дождь",
        "Легкий дождь",
        "Умеренный дождь",
        "Сильный дождь",
        "Ледяной дождь",
        "Ледяной дождь",
        "Возможен снег",
        "Снег",
        "Сильный снегопад",
        "Град",
        "Возможен ливень",
        "Умеренный ливень",
        "Сильный ливень",
        "Снежный дождь",
        "Снежный дождь",
        "Гроза",
        "Гроза",
        "Гроза"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = this.activity?.getSharedPreferences(PREF,Context.MODE_PRIVATE) ?: return
        city = pref.getString(KEY,"Moscow").toString()
        initRv()
        geocoding(city)
        //requestWeather(city)

        //кнопка выбора местоположения
        binding.btnLocation.setOnClickListener {
            DialogManager.search(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String) {
                    city = name
                    pref.edit().putString(KEY,city).apply()
                    geocoding(city)
                }

            })
        }
    }


    //инициализация RecycleView
    private fun initRv() = with(binding) {
        holder.layoutManager =
                //делаем RecycleView горизонтальным
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = RvAdapter()
        holder.adapter = adapter
        //Получение данных в RcView
        model.liveDataList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    //обновление данных из weatherNow
    private fun updateCurrent() {
        binding.tvLocation.text = city
        model.liveDataNow.observe(viewLifecycleOwner) {
           // var date: String = it.date.substring(10, 16)
            Toast.makeText(activity, "Данные обновлены", Toast.LENGTH_SHORT).show()

            binding.tvTemp.text = it.temp.toFloat().toInt().toString() + "°C"
            var i = 0
            for (i in 0 until eng.size - 1) {
                if (it.conditions.equals(eng[i])) {
                    binding.tvConditions.text = rus[i]
                }
            }
            var wind = mainObj.getJSONObject("current_weather").getString("windspeed")
            binding.tvWind.text = wind.toFloat().toInt().toString() + " м/с"
            val hourArr = mainObj.getJSONObject("hourly").getJSONArray("time")
            val humArr = mainObj.getJSONObject("hourly").getJSONArray("relativehumidity_2m")
            val presArr = mainObj.getJSONObject("hourly").getJSONArray("surface_pressure")
            var date = mainObj.getJSONObject("current_weather").getString("time")
            //Log.d("My",hourArr.toString())
            for (i in 0 until hourArr.length()-1){
                if (date.equals(hourArr[i])){
                    binding.tvHum2.text = humArr[i].toString() + " %"
                    var pres = ((presArr[i].toString().toFloat() * 736.1) / 1000).toInt().toString()
                    binding.pressure.text = "Атмосферное давление: $pres мм рт.cт."
                }
            }
            //binding.tvConditions.text = it.conditions
            //Picasso.get().load("https:" + it.img).into(binding.imToday)
        }
    }

    private fun geocoding(city: String){
        val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=$city&count=1&language=ru"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,             //при успешном ответе - парсим данные
            geoUrl, { result ->
                var geoObj = JSONObject(result)
                latitude = geoObj.getJSONArray("results").getJSONObject(0).getString("latitude").toFloat()
                longitude = geoObj.getJSONArray("results").getJSONObject(0).getString("longitude").toFloat()
                Log.d("My","lat=$latitude and long=$longitude")
                requestWeather(latitude,longitude)
            },
            { error ->                      // при ошибке показываем код ошибки
                Toast.makeText(context, "Error \nTry again", Toast.LENGTH_SHORT).show()
            })
        queue.add(request)
    }

    //Получаем данные с сервера
    private fun requestWeather(latitude:Float,longitude:Float) {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&hourly=temperature_2m,relativehumidity_2m,apparent_temperature,weathercode,surface_pressure,windspeed_10m&daily=weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset&current_weather=true&windspeed_unit=ms&timezone=auto"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,             //при успешном ответе - парсим данные
            url, { result ->
                parseWeatherData(result)
            },
            { error ->                      // при ошибке показываем код ошибки
                Toast.makeText(context, "Error \nTry again", Toast.LENGTH_SHORT).show()
            })
        queue.add(request)
        updateCurrent()
    }

    //парсим результат
    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        mainObj = JSONObject(result)
        val list = parseForecast(mainObject)
        parseCurrentDay(mainObject, list[0])
    }

    //Получение данных по текущей погоде
    private fun parseCurrentDay(mainObject: JSONObject, weatherItem: WeatherNow) {
    //private fun parseCurrentDay(mainObject: JSONObject,weatherItem:List<WeatherNow>) {
        val item = WeatherNow(
            city,
            mainObject.getJSONObject("current_weather").getString("temperature"),
            weatherItem.conditions,
            "",
            weatherItem.date,
            weatherItem.minTemp,
            weatherItem.maxTemp,
            "",
            "",
            ""
        )
        model.liveDataNow.value = item
    }

    //парсим данные по ближайшим датам
    private fun parseForecast(mainObject: JSONObject): List<WeatherNow> {
        val list = ArrayList<WeatherNow>()
        //val name = mainObject.getJSONObject("location").getString("name")
        val days = mainObject.getJSONObject("daily")
        val timeArr = days.getJSONArray("time")
        val weatherCodeArr = days.getJSONArray("weathercode")
        val temperatureMaxArr = days.getJSONArray("temperature_2m_max")
        val temperatureMinArr = days.getJSONArray("temperature_2m_min")
        //.getJSONArray("forecastday")
        for (i in 0 until timeArr.length()) {
            //val day = daysArray[i] as JSONObject
            val item = WeatherNow(
                city,
                "",
                weatherCodeArr[i].toString(),
                "",
                timeArr[i].toString(),
                temperatureMinArr[i].toString(),
                temperatureMaxArr[i].toString(),
                "", "", ""
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }


    companion object {
        @JvmStatic
        fun newInstance() = TodayFrag()
    }
}