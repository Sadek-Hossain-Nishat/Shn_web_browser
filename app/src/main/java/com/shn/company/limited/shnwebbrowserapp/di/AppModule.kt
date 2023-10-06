package com.shn.company.limited.shnwebbrowserapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.shn.company.limited.shnwebbrowserapp.LocationData
import com.shn.company.limited.shnwebbrowserapp.api.WeatherApi
import com.shn.company.limited.shnwebbrowserapp.db.locationDataStore
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofie():Retrofit =
        Retrofit.Builder()
            .baseUrl(WeatherApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit):WeatherApi =
        retrofit.create(WeatherApi::class.java)



    @Provides
    @Reusable
    fun provideProtoDataStore(@ApplicationContext context: Context):DataStore<LocationData>{

        return context.locationDataStore

    }




}