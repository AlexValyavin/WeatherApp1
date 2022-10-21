package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ForecastItemBinding
import com.squareup.picasso.Picasso

class RvAdapter : ListAdapter<WeatherNow,RvAdapter.Holder>(Comparator()) {
    class Holder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ForecastItemBinding.bind(view)
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
        val rus = listOf("Солнечно",
            "Ясно",
            "Переменная облачность",
            "Облачно",
            "Пасмурно",
            "Туман",
            "Легкий дождь",
            "Легкий снег",
            "Мокрый снег",
            "Дождь",
            "Вспышки грома",
            "Метель",
            "Метель",
            "Туман",
            "Морозный туман",
            "Легкий дождь",
            "Легкий дождь",
            "Морозный дождь",
            "Сильный дождь",
            "Легкий дождь",
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
            "Сильный дождь со снегом",
            "Снежный дождь",
            "Сильный снежный дождь",
            "Легкий град",
            "Сильный град",
            "Небольшой дождь с громом",
            "Сильный дождь с громом",
            "Небольшой снег с громом",
            "Сильный снег с громом")

        fun bind(item:WeatherNow){
            binding.tvDate.text = item.date

            for (i in 0 until eng.size-1){
                if (item.conditions.equals(eng[i])){
                    binding.tvConditionSmall.text = rus[i]
                }
            }
            //binding.tvConditionSmall.text = item.conditions
            Picasso.get().load("https:"+item.img).into(binding.ImConditionSmall)
            binding.tvMax.text = item.maxTemp.toFloat().toInt().toString()+"°C"
            binding.tvMin.text = item.minTemp.toFloat().toInt().toString()+"°C"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.forecast_item,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}

class Comparator:DiffUtil.ItemCallback<WeatherNow>(){
    override fun areItemsTheSame(oldItem: WeatherNow, newItem: WeatherNow): Boolean {
        return  oldItem==newItem
    }

    override fun areContentsTheSame(oldItem: WeatherNow, newItem: WeatherNow): Boolean {
        return  oldItem==newItem
    }

}