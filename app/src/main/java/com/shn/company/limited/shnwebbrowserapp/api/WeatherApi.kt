package com.shn.company.limited.shnwebbrowserapp.api

import com.shn.company.limited.shnwebbrowserapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    companion object {
        const val BASE_URL = "https://api.weatherapi.com/"
        const val API_KEY = BuildConfig.WEATHER_API_KEY
    }

    @GET("v1/current.json")
    suspend fun getWeather(
        @Query("key")  apiKey:String,
        @Query("q")  location:String
    ):WeatherResponse





}