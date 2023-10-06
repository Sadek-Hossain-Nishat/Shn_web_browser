package com.shn.company.limited.shnwebbrowserapp.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.RemoteException
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shn.company.limited.shnwebbrowserapp.LocationData
import com.shn.company.limited.shnwebbrowserapp.network.CheckInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class HomeViewModel @Inject constructor(val checkInternetConnection: CheckInternetConnection,
                                        locationDataStore:DataStore<LocationData>,
   @ApplicationContext val context: Context):ViewModel() {
    fun editInterStatus() {
        runtimestatuscheck.value =" "
    }


    private val homeeventChannel = Channel<HomepageEvent>()

    val homepageEvents = homeeventChannel.receiveAsFlow().flowOn(Dispatchers.Main)

    companion object{
        const val OFFLINE="offline"
        const val ONLINE="online"
    }




    private val _locationData = MutableStateFlow<LocationModel>(LocationModel.defaultInstance)


    val locationData:StateFlow<LocationModel> = _locationData

    private val _runtimeStatusCheck = MutableStateFlow<Boolean>(checkInternetConnection.internetStatus())

     val runtimestatuscheck = MutableStateFlow<String>("")



    /*important code*/

    /*
   important
   * */
    private val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var callback: ConnectivityManager.NetworkCallback? = null
//    private var receiver: ConnectivityReceiver? = null

//    private val _state = MutableStateFlow(getCurrentNetwork())


    private val _share = MutableSharedFlow<NetworkStatusState>()



//    val state: SharedFlow<NetworkStatusState> = _state

    val share: SharedFlow<NetworkStatusState> = _share




    init {

        viewModelScope.launch {

//            _runtimeStatusCheck.subscriptionCount
//                .map {
//
//                    count -> count>0
//
//                }
//                .distinctUntilChanged()
//                .onEach {
//
//
//                    Log.d("internetcheck", ": $it")
//
//
//                    if (_runtimeStatusCheck.value){
//
//                        if (runtimestatuscheck.value == OFFLINE) {
//                            runtimestatuscheck.value = ONLINE
//                            homeeventChannel.send(HomepageEvent.InternetstatusyesafternoeventinAppRuntime)
//
//                        }else{
//
//
//                            homeeventChannel.send(HomepageEvent.InternetstatusyesEvent)
//
//                        }
//
//
//
//
//
//
//
//
//                    }
//                    else{
//                        locationDataStore.data.map {
//                            _locationData.value = LocationModel(latitude = it.latitude, longitude = it.longitude)
//                        }
//                        runtimestatuscheck.value  = OFFLINE
//                        homeeventChannel.send(HomepageEvent.InternetstatusnoEvent)
//                    }
//
//                }
//
//




//            _state
//                .subscriptionCount
//                .map { count -> count > 0 } // map count into active/inactive flag
//                .distinctUntilChanged() // only react to true<->false changes
//                .onEach { isActive ->
//                    Log.d("callback", ": "+isActive)
//                    /** Only subscribe to network callbacks if we have an active subscriber */
//                    if (isActive) subscribe()
//                    else unsubscribe()
//                }.launchIn(CoroutineScope(SupervisorJob() + Dispatchers.Main))


//            _share.emit(getCurrentNetwork())
            _share

                .subscriptionCount
                .map { count -> count > 0 } // map count into active/inactive flag
                .distinctUntilChanged() // only react to true<->false changes
                .onEach { isActive ->
                    Log.d("callback", ": "+isActive)
                    /** Only subscribe to network callbacks if we have an active subscriber */
                    if (isActive) subscribe()
                    else unsubscribe()
                }.launchIn(CoroutineScope(SupervisorJob() + Dispatchers.Main))






        }


    }

    private fun unsubscribe() {
        if (callback == null) return

        callback?.run { cm.unregisterNetworkCallback(this) }
        callback = null
    }

    private fun subscribe() {
        // just in case
        if (callback != null) return

        callback = NetworkCallbackImpl().also { cm.registerDefaultNetworkCallback(it) }

        /* emit our initial state */
        emitNetworkState(getCurrentNetwork())
    }


    /* Simple getter for fetching network connection status synchronously */
    fun hasNetworkConnection() = getCurrentNetwork() == NetworkStatusState.NetworkStatusConnected


    private fun getCurrentNetwork(): NetworkStatusState {
        return try {
            cm.getNetworkCapabilities(cm.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .let { connected ->
                    if (connected == true) NetworkStatusState.NetworkStatusConnected
                    else NetworkStatusState.NetworkStatusDisconnected
                }
        } catch (e: RemoteException) {
            NetworkStatusState.NetworkStatusDisconnected
        }
    }


    private fun emitNetworkState(newState: NetworkStatusState) {
        viewModelScope.launch(Dispatchers.Main) {
//            _state.emit(newState)
            _share.emit(newState)
        }
    }



    sealed class NetworkStatusState {

        /* Device has a valid internet connection */
        object NetworkStatusConnected : NetworkStatusState()

        /* Device has no internet connection */
        object NetworkStatusDisconnected : NetworkStatusState()
    }


    private inner class NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) = emitNetworkState(NetworkStatusState.NetworkStatusConnected)

        override fun onLost(network: Network) = emitNetworkState(NetworkStatusState.NetworkStatusDisconnected)
    }




    sealed class HomepageEvent{

        object InternetstatusyesEvent: HomepageEvent()
        object InternetstatusnoEvent: HomepageEvent()

        object InternetstatusyesafternoeventinAppRuntime : HomepageEvent()

    }










}