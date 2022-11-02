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

        fun bind(item:WeatherNow){
            binding.tvDate.text = item.date

            for (i in 0 until eng.size-1){
                if (item.conditions.equals(eng[i])){
                    binding.tvConditionSmall.text = rus[i]
                }
            }
            //binding.tvConditionSmall.text = item.conditions
            //Picasso.get().load("https:"+item.img).into(binding.ImConditionSmall)
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