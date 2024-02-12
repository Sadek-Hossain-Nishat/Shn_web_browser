package com.shn.company.limited.shnwebbrowserapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone

import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ResolvableApiException

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Clock
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class LocationViewModel @Inject constructor(@ApplicationContext private val context:Context): ViewModel() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val DATE="date"
    private val TIME="time"


    private val locationRequestChannel = Channel<LocationRequestIntent> ()




    val locationRequestEvents = locationRequestChannel.receiveAsFlow().flowOn(Dispatchers.Main)
    private val _currentDate = MutableStateFlow<String>("")
    private val _currentTime = MutableStateFlow<String>("")


  val currentDate: StateFlow<String> = _currentDate
  val currentTime: StateFlow<String> = _currentTime



    init {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


        getCurrentDate()
        getCurrentTime()
        
        /***

        _currentDate.value = getCurrentDate(DATE)
        _currentTime.value = getCurrentDate(TIME)
        
        
        ***/
    }



    @SuppressLint("NewApi", "SimpleDateFormat")
    private fun getCurrentTime(){
//        val utc = TimeZone.getTimeZone("etc/UTC")

        val clock = Clock.systemDefaultZone()


        val utc = TimeZone.getTimeZone(clock.zone.toString())





        val serverSDF = SimpleDateFormat("HH:mm:ss aa",

       )


        serverSDF.timeZone = utc



        println("timezone => ${clock.zone}")


        viewModelScope.launch {

            flow {
                while(true){
                  val calfortime: Calendar = Calendar.getInstance()


                    emit(serverSDF.format(calfortime.time))
//                    emit(localSDF.format(serverSDF.parse(calfortime.time.toString())))

                    println("current time=>${serverSDF.format(calfortime.time)}")
//                    println("current time=>${calfortime.}")

                    kotlinx.coroutines.delay(1000)
                }
            }
                .collect{


                   _currentTime.value = it
                }


        }







    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(
//        type: String
    ): Unit {




        val monthDateYear = SimpleDateFormat("MMMM dd, YYYY")





        viewModelScope.launch {







                    flow {
                        while(true){
                            val calfordate: Calendar = Calendar.getInstance()
                            emit(monthDateYear.format(calfordate.time))

                            kotlinx.coroutines.delay(86400000)
                        }
                    }
                        .collect{


                            _currentDate.value = it
                        }

                /***
                TIME->{
                    flow {
                        while (true) {
                            emit(currentTime.format(cal.time))
                        }
                    }.collect{
                        _currentTime.value = it
                    }
                }

                ***/


        }







        /***

       return  when(type){
            DATE ->{
                monthDateYear.format(cal.time)


            }
            TIME->{

                 currentTime.format(cal.time)


            }

           else -> {""}
       }


        ***/





    }

    fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        val builder = locationRequest?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> =
            builder?.let { client.checkLocationSettings(it.build()) } as Task<LocationSettingsResponse>

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            if (locationSettingsResponse.locationSettingsStates?.isLocationPresent == true) {
                Toast.makeText(
                    context,
                    "Location settings is  already enabled",
                    Toast.LENGTH_LONG
                ).show()
                findLoaction()
            } else Toast.makeText(
                context,
                "Location settings has been enabled",
                Toast.LENGTH_LONG
            ).show()


        }

        task.addOnFailureListener { exception ->


            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.

                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().

//                    exception.startResolutionForResult(requireActivity(),
//                        200)

            /***
                startIntentSenderForResult(
                        exception.resolution.intentSender,
                        200,
                        null,
                        0,
                        0,
                        0,
                        null
                    );

                    ***/
                    viewModelScope.launch {

                        locationRequestChannel.send(LocationRequestIntent.IntentforLocationRequest(exception))

                    }



                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }



    @SuppressLint("MissingPermission")
    fun findLoaction() {


        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {

                val geocoder = Geocoder(context, Locale.getDefault())
                val addressess = geocoder.getFromLocation(
                    location.latitude, location.longitude,
                    1
                )

                val city = addressess!![0].locality
                val addresline = addressess!![0].getAddressLine(0)


                val countryname = addressess!![0].countryName


//                Toast.makeText(requireContext(),"Location ${location.latitude} ${location.longitude}",Toast.LENGTH_LONG).show()
//                Toast.makeText(requireContext(),"Location ${addresline}",Toast.LENGTH_LONG).show()
                Toast.makeText(context, "Location ${addressess}", Toast.LENGTH_LONG).show()


            } else {
                Toast.makeText(context, "Location not found", Toast.LENGTH_LONG).show()
            }


        }


    }

    sealed class LocationRequestIntent{
       data class IntentforLocationRequest(val exception: ResolvableApiException):LocationRequestIntent()
    }



}