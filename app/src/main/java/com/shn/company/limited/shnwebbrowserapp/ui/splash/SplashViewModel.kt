package com.shn.company.limited.shnwebbrowserapp.ui.splash



import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shn.company.limited.shnwebbrowserapp.db.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SplashViewModel
@Inject
constructor
(
    private val preferencesDataStore: PreferencesDataStore
        )

    : ViewModel() {

    /***

    private var _progressingValue = MutableLiveData<Int>(0)

    val progressing: LiveData<Int> = _progressingValue

     ***/


    // livedata and stateflow for single data they act similar so
    // we can use stateflow insted of livedata
    // because stateflow is latest features in modern android development

 //using flow
    private var _progressingValue = MutableStateFlow<Int>(0)
    val progressing: StateFlow<Int> = _progressingValue

    private var _hideprogress = MutableStateFlow<Boolean>(true)

    val hideprogress = _hideprogress






    // to initialte splash event you have to create a channel for SplashEvent

    private val splashEventChannel = Channel<SplashEvent>()

    val splashEvent = splashEventChannel.receiveAsFlow().flowOn(Dispatchers.Main)


    val willStart = true





    init {

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

            preferencesDataStore.firsttimerunFlow.flowOn(Dispatchers.Main).collect{
                firsttimerun->
                 if (firsttimerun)
                {
                    _hideprogress.value = false










                     flow<Int> {
                         for (i in 0..100 step 33) {
                             delay(1000)
                             emit(i)
                         }
                     }.flowOn(Dispatchers.Main)
                         .collect {


                             _progressingValue.value = _progressingValue.value+33




                             Log.i("progvalue", "${progressing.value}")


















                         }

                     withContext(Dispatchers.Default){


                         splashEventChannel.send(SplashEvent.NavigateFromSplashToFirstConfigScreen)

                     }



//                     to(
//
//
//                             splashEventChannel.send(SplashEvent.NavigateFromSplashToFirstConfigScreen)
//
//
//                             )



                }
                else splashEventChannel.send(SplashEvent.NavigateFromSplashToHomeScreen)

            }




        }




    }

    // splash event class
    sealed class SplashEvent {
        object NavigateFromSplashToFirstConfigScreen : SplashEvent()
        object NavigateFromSplashToHomeScreen : SplashEvent()
    }

    companion object {

       private const val CURRENT_PROGRESSING_VALUE ="current_progressingvalue"
       private const val INITIAL_PROGRESSING_VALUE =0



    }










}

