package com.example.weatherapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val liveDataNow = MutableLiveData<WeatherNow>()
    val liveDataList = MutableLiveData<List<WeatherNow>>()
}