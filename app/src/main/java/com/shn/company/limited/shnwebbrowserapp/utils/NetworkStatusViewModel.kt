package com.shn.company.limited.shnwebbrowserapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shn.company.limited.shnwebbrowserapp.LocationData
import com.shn.company.limited.shnwebbrowserapp.ui.home.LocationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkStatusViewModel @Inject constructor(
//    val checkInternetConnection: CheckInternetConnection,
                                        locationDataStore:DataStore<LocationData>,
   @ApplicationContext val context: Context):ViewModel() {




//    var countOnline= 0
    var countOffline= 0

    private val networkeventChannel = Channel<NetworkEvent>()

    val networkEvent = networkeventChannel.receiveAsFlow().flowOn(Dispatchers.Main)







    private val _locationData = MutableStateFlow<LocationModel>(LocationModel.defaultInstance)


    val locationData:StateFlow<LocationModel> = _locationData








    private val  _networkstatus = MutableStateFlow<NetworkEvent>(NetworkEvent.NetworkIsDisconnected)


    val networkstatus:StateFlow<NetworkEvent> = _networkstatus



    val networkcallback=

        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network : Network) {

                viewModelScope.launch {
                    _networkstatus.value = NetworkEvent.NetworkIsConnected



                    if (countOffline==1) {
                        networkeventChannel.send(NetworkEvent.NetworkIsConnected)
                        countOffline = 0
                    }

                }



            }

            override fun onLost(network : Network) {

                viewModelScope.launch {

                    _networkstatus.value = NetworkEvent.NetworkIsDisconnected
                    countOffline = 1
                    networkeventChannel.send(NetworkEvent.NetworkIsDisconnected)





                }

            }



        }







    val connectivityManager = context.getSystemService(ConnectivityManager::class.java)





    init {

        viewModelScope.launch {


            connectivityManager.registerDefaultNetworkCallback(networkcallback)

            if (connectivityManager.activeNetwork == null) {

                networkeventChannel.send(NetworkEvent.NetworkIsDisconnected)
                countOffline = 1

            }



        }


    }
















    override fun onCleared() {
        super.onCleared()
        Log.d("viewmodel", "onCleared: ")
        connectivityManager.unregisterNetworkCallback(networkcallback)
    }

    fun clearAll() {

        Log.d("viewmodel", "clearAll:  ")
    }


    sealed class NetworkEvent{
        object NetworkIsConnected: NetworkEvent()
        object NetworkIsDisconnected: NetworkEvent()

    }





}