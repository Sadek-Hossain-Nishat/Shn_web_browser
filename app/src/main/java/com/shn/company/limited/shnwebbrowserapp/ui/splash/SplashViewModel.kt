package com.shn.company.limited.shnwebbrowserapp.ui.splash


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shn.company.limited.shnwebbrowserapp.db.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SplashViewModel
@Inject
constructor
    (
    private val preferencesDataStore: PreferencesDataStore,
    private val savestateHandler: SavedStateHandle,
)

    : ViewModel() {

    /***

    private var _progressingValue = MutableLiveData<Int>(0)

    val progressing: LiveData<Int> = _progressingValue

     ***/


    // livedata and stateflow for single data they act similar so
    // we can use stateflow insted of livedata
    // because stateflow is latest features in modern android development
    private val PROGRESSING_KEY = "progressbar_value"

    private val HIDE_VIEW_KEY = "hide_view_value"
    private val IS_PAUSE = "ispause"

    //using flow
    private var _progressingValue =
        MutableStateFlow<Int>(savestateHandler.getStateFlow(PROGRESSING_KEY, 0).value)

//        var progressing: StateFlow<Int> = savestateHandler.getStateFlow(PROGRESSING_KEY,0)
    var progressing: StateFlow<Int> = _progressingValue

    private var _hideprogress =
        MutableStateFlow<Boolean>(savestateHandler.getStateFlow(HIDE_VIEW_KEY, true).value)

//        val hideprogress = savestateHandler.getStateFlow(HIDE_VIEW_KEY,true)
    val hideprogress = _hideprogress

    private var _ispause = MutableStateFlow<Boolean>(savestateHandler.getStateFlow(IS_PAUSE,false).value)

    val ispause = _ispause


    // to initialte splash event you have to create a channel for SplashEvent

    private val splashEventChannel = Channel<SplashEvent>()

    val splashEvent = splashEventChannel.receiveAsFlow().flowOn(Dispatchers.Main)


    val willStart = true


    init {

        Log.d(
            "debugval",
            ": first startup " + savestateHandler[PROGRESSING_KEY] + "  " + savestateHandler[HIDE_VIEW_KEY]
        )
        Log.d("debugval", "Testingvalue : " + progressing.value + "  " + hideprogress.value)

        // if you use view model
        /***

        viewModelScope.launch {
        while (true) {
        delay(1000)
        if (_progressingValue.value!! < 100) {
        _progressingValue.postValue(_progressingValue.value!! + 30)
        } else {

        splashEventChannel.send(SplashEvent.NavigateToHomeScreen)

        }
        }
        }
         ***/


        viewModelScope.launch {

            preferencesDataStore.firsttimerunFlow.flowOn(Dispatchers.Main).collect { firsttimerun ->
                if (firsttimerun) {
//                    _hideprogress.value = false

//                     savestateHandler[HIDE_VIEW_KEY] = false
                    _hideprogress.value = !savestateHandler.getStateFlow(HIDE_VIEW_KEY, true).value










                    flow<Int> {
                        for (i in (1+savestateHandler.getStateFlow(PROGRESSING_KEY,0).value)..(100-savestateHandler.getStateFlow(PROGRESSING_KEY,0).value) step 33) {
                            delay(1000)

                            emit(i)
                        }
                    }.flowOn(Dispatchers.Main)
                        .collect {


//                             savestateHandler[PROGRESSING_KEY] = it
                            _progressingValue.value = it




                            Log.i("debugval", "progvalue = > ${progressing.value}")


//                             if (progressing.value >= 100) {
//
//
//                                 splashEventChannel.send(SplashEvent.NavigateFromSplashToFirstConfigScreen)
//
//                             }


                        }

                     withContext(Dispatchers.Main){

                        splashEventChannel.send(SplashEvent.NavigateFromSplashToFirstConfigScreen)


                     }


//                     to(
//
//
//                             splashEventChannel.send(SplashEvent.NavigateFromSplashToFirstConfigScreen)
//
//
//                             )


                } else splashEventChannel.send(SplashEvent.NavigateFromSplashToHomeScreen)

            }


        }


    }

    // splash event class
    sealed class SplashEvent {
        object NavigateFromSplashToFirstConfigScreen : SplashEvent()
        object NavigateFromSplashToHomeScreen : SplashEvent()
    }

    companion object {

        private const val CURRENT_PROGRESSING_VALUE = "current_progressingvalue"
        private const val INITIAL_PROGRESSING_VALUE = 0


    }



    fun onResumeEvent() {

       if (ispause.value) {


           viewModelScope.launch {

               Log.d("debugval", "onResumeEvent: started")


               preferencesDataStore.firsttimerunFlow.flowOn(Dispatchers.Main)
                   .collect { firsttimerun ->
                       if (firsttimerun) {


                           if (_progressingValue.value >= 100) {


                               splashEventChannel.send(SplashEvent.NavigateFromSplashToFirstConfigScreen)


                           }


                       } else {


                           splashEventChannel.send(SplashEvent.NavigateFromSplashToHomeScreen)


                       }
                   }
           }
       }

        else println()



    }

    fun onPauseEvent() {
        savestateHandler[PROGRESSING_KEY] = _progressingValue.value
        savestateHandler[HIDE_VIEW_KEY] = _hideprogress.value
        savestateHandler[IS_PAUSE] = true
    }






}

