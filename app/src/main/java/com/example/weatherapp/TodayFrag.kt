package com.example.weatherapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.databinding.FragmentTodayBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API = "8f55e87ff1e7424696075013221910"

class TodayFrag : Fragment() {
    private lateinit var binding: FragmentTodayBinding
    private val model: MainViewModel by activityViewModels()
    private lateinit var adapter: RvAdapter
    private var city = "Moscow"
    private lateinit var pLauncher: ActivityResultLauncher<String>

    val eng = listOf("Sunny",
        "Clear",
        "Partly cloudy",
        "Cloudy",
        "Overcast",
        "Mist",
        "Patchy rain possible",
        "Patchy snow possible",
        "Patchy sleet possible",
        "Patchy freezing drizzle possible",
        "Thundery outbreaks possible",
        "Blowing snow",
        "Blizzard",
        "Fog",
        "Freezing fog",
        "Patchy light drizzle",
        "Light drizzle",
        "Freezing drizzle",
        "Heavy freezing drizzle",
        "Patchy light rain",
        "Light rain",
        "Moderate rain at times",
        "Moderate rain",
        "Heavy rain at times",
        "Heavy rain",
        "Light freezing rain",
        "Moderate or heavy freezing rain",
        "Light sleet",
        "Moderate or heavy sleet",
        "Patchy light snow",
        "Light snow",
        "Patchy moderate snow",
        "Moderate snow",
        "Patchy heavy snow",
        "Heavy snow",
        "Ice pellets",
        "Light rain shower",
        "Moderate or heavy rain shower",
        "Torrential rain shower",
        "Light sleet showers",
        "Moderate or heavy sleet showers",
        "Light snow showers",
        "Moderate or heavy snow showers",
        "Light showers of ice pellets",
        "Moderate or heavy showers of ice pellets",
        "Patchy light rain with thunder",
        "Moderate or heavy rain with thunder",
        "Patchy light snow with thunder",
        "Moderate or heavy snow with thunder")

    val rus = listOf(" Солнечно",
        "Ясно",
        "Переменная облачность",
        "Облачно",
        "Пасмурно",
        "Туман",
        "Кратковременный дождь",
        "Кратковременный снег",
        "Кратковременный мокрый снег",
        "Кратковременный моросящий дождь",
        "Вспышки грома",
        "Метель",
        "Метель",
        "Туман",
        "Морозный туман",
        "Легкий дождь",
        "Легкий дождь",
        "Морозный дождь",
        "Сильный дождь",
        "Неравномерный легкий дождь",
        "Небольшой дождь",
        "Умеренный дождь",
        "Умеренный дождь",
        "Сильный дождь",
        "Сильный дождь",
        "Ледяной дождь",
        "Ледяной дождь",
        "Мокрый снег",
        "Мокрый снег",
        "Небольшой снег",
        "Небольшой снег",
        "Умеренный снег",
        "Умеренный снег",
        "Сильный снег",
        "Сильный снег",
        "Град",
        "Легкий ливень",
        "Умеренный ливень",
        "Проливной дождь",
        "Дождь с мокрым снегом",
        "Сильный дождь со снегом",
        "Снежный дождь",
        "Сильный снежный дождь",
        "Легкий град",
        "Сильный град",
        "Небольшой дождь с громом",
        "Сильный дождь с громом",
        "Небольшой снег с громом",
        "Сильный снег с громом")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        requestWeather(city)

        //кнопка выбора местоположения
        binding.btnLocation.setOnClickListener {
            DialogManager.search(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String) {
                    city = name
                    requestWeather(city)
                }

            })
        }
    }



    //инициализация RecycleView
    private fun initRv() = with(binding) {
        holder.layoutManager =
                //делаем RecycleView горизонтальным
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        adapter = RvAdapter()
        holder.adapter = adapter
        //Получение данных в RcView
        model.liveDataList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    //обновление данных из weatherNow
    private fun updateCurrent() {
        model.liveDataNow.observe(viewLifecycleOwner) {
            binding.tvLocation.text = city
            var date: String = it.date.substring(10, 16)
            Toast.makeText(activity, "${getString(R.string.updated_time)} $date", Toast.LENGTH_SHORT).show()

            binding.tvTemp.text = it.temp.toFloat().toInt().toString() + "°C"
            var i = 0
            for (i in 0 until eng.size-1){
                if (it.conditions.equals(eng[i])){
                    binding.tvConditions.text = rus[i]
                }
            }
            binding.tvWind.text = (it.wind.toFloat()/3.6).toInt().toString()+" м/с"
            binding.tvHum2.text = it.hum+" %"
            var pres = ((it.pressure.toFloat() * 736.1)/1000).toInt().toString()
            binding.pressure.text = "Атмосферное давление: $pres мм рт.cт."
            //binding.tvConditions.text = it.conditions
            Picasso.get().load("https:" + it.img).into(binding.imToday)
        }
    }

    //Получаем данные с сервера
    private fun requestWeather(city: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=$API&q=$city&days=8"
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
        val list = parseForecast(mainObject)
        parseCurrentDay(mainObject, list[0])
    }

    //Получение данных по текущей погоде
    private fun parseCurrentDay(mainObject: JSONObject, weatherItem: WeatherNow) {
        val item = WeatherNow(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("temp_c"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            mainObject.getJSONObject("current").getString("last_updated"),
            weatherItem.maxTemp, weatherItem.minTemp,
            mainObject.getJSONObject("current").getString("wind_kph"),
            mainObject.getJSONObject("current").getString("humidity"),
            mainObject.getJSONObject("current").getString("pressure_mb")
        )
        model.liveDataNow.value = item
    }

    //парсим данные по ближайшим датам
    private fun parseForecast(mainObject: JSONObject): List<WeatherNow> {
        val list = ArrayList<WeatherNow>()
        val name = mainObject.getJSONObject("location").getString("name")
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherNow(
                name,
                day.getJSONObject("day").getString("avgtemp_c"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getString("date"),
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONObject("day").getString("maxtemp_c"),
            "","","")
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