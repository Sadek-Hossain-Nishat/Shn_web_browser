package com.shn.company.limited.shnwebbrowserapp.ui.firsttimeconfiguration

import android.graphics.Color
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
import kotlin.random.Random

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val iconRepository: IconRepository,
    private val preferencesDataStore: PreferencesDataStore,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(

) {

    private val BOUNCING_COMMAND = "bouncing_command"
    private val RANDOM_COLOR = "random_color"

    //   private val  FIRST_TIME_CONFIGURATION_MODEL =""
    private val FIRST_TIME_CONFIGURATION_MODEL_ICON_NAME = ""
    private val FIRST_TIME_CONFIGURATION_MODEL_ICON = ""

    private val IS_HIDE = "is_hide"
    private val IS_ANIMATE_START = "is_animate_start"


    // livedata and stateflow for single data they act similar so
    // we can use stateflow insted of livedata
    // because stateflow is latest features in modern android development


    //    private var _bouncingCommand = MutableStateFlow<Boolean>(false)
    private var _bouncingCommand =
        MutableStateFlow<Boolean>(savedStateHandle.getStateFlow(BOUNCING_COMMAND, false).value)

    //
//
    val bouncingCommand: StateFlow<Boolean> = _bouncingCommand
//    val bouncingCommand: StateFlow<Boolean> = savedStateHandle.getStateFlow(BOUNCING_COMMAND, false)
//   private var _randomColor =MutableStateFlow<String>(
//       java.lang.String.format(
//           "#%06X",
//           0xFFFFFF and Color.argb(255,Random.nextInt(0,256),Random.nextInt(0,256),
//           Random.nextInt(0,256))
//       ))

    //    private var _randomColor = MutableStateFlow<Int>(
//        Color.argb(
//            255, Random.nextInt(0, 256), Random.nextInt(0, 256),
//            Random.nextInt(0, 256)
//        )
//    )
    private var _randomColor = MutableStateFlow<Int>(
        savedStateHandle.getStateFlow(
            RANDOM_COLOR,
            Color.argb(
                255, Random.nextInt(0, 256), Random.nextInt(0, 256),
                Random.nextInt(0, 256)
            )
        ).value
    )

    //
//
    val randromColor: StateFlow<Int> = _randomColor
//    val randromColor: StateFlow<Int> = savedStateHandle.getStateFlow(
//        RANDOM_COLOR,
//        Color.argb(
//            255, Random.nextInt(0, 256), Random.nextInt(0, 256),
//            Random.nextInt(0, 256)
//        )
//    )



    private var _firsttimeConfigurationModel =
        MutableStateFlow<FirstTimeConfigurationModel>(
            FirstTimeConfigurationModel(

               "",
                0


            )
        )


    val firstTimeConfigurationModel: StateFlow<FirstTimeConfigurationModel> =
        _firsttimeConfigurationModel



    // this process is not right so we comment out

//    val firstTimeConfigurationModel2: StateFlow<FirstTimeConfigurationModel> =
//        savedStateHandle.getStateFlow(FIRST_TIME_CONFIGURATION_MODEL,
//            FirstTimeConfigurationModel("",0)
//        )


    //    private var _isHide = MutableStateFlow<Boolean>(true)
    private var _isHide =
        MutableStateFlow<Boolean>(savedStateHandle.getStateFlow(IS_HIDE, true).value)

    //
//
    val isHide: StateFlow<Boolean> = _isHide
//    val isHide: StateFlow<Boolean> = savedStateHandle.getStateFlow(IS_HIDE, true)


    //    private var _isAnimstart = MutableStateFlow<Boolean>(false)
    private var _isAnimstart =
        MutableStateFlow<Boolean>(savedStateHandle.getStateFlow(IS_ANIMATE_START, false).value)

    //
    val isAnimstart: StateFlow<Boolean> = _isAnimstart
//    val isAnimstart: StateFlow<Boolean> = savedStateHandle.getStateFlow(IS_ANIMATE_START, false)


    private val firstconfigtohomeChannel = Channel<FirstConfigEvent>()

    val firstconfigtoHomeEvent = firstconfigtohomeChannel.receiveAsFlow().flowOn(Dispatchers.Main)


    init {


        viewModelScope.launch {


            flow<FirstTimeConfigurationModel> {

                iconRepository.firsttimelist

                .
            forEach {

                    delay(1000L)


                    emit(it)


                }
            }.flowOn(Dispatchers.Main)
                .collect {

//                    savedStateHandle[RANDOM_COLOR] = Color.argb(
//                        255, Random.nextInt(0, 256), Random.nextInt(0, 256),
//                        Random.nextInt(0, 256)
//                    )


                    _randomColor.value =
                        Color.argb(
                            255, Random.nextInt(0, 256), Random.nextInt(0, 256),
                            Random.nextInt(0, 256)
                        )
                    _bouncingCommand.value = true


//                    savedStateHandle[RANDOM_COLOR] = Color.argb(
//                        255, Random.nextInt(0, 256), Random.nextInt(0, 256),
//                        Random.nextInt(0, 256)
//                    )
//
//                    savedStateHandle[BOUNCING_COMMAND] = true
//

                    _firsttimeConfigurationModel.value = it

                    //wrong code so comment out

//                    Log.d("seeobj", ": "+firstTimeConfigurationModel2.toString())

//                    savedStateHandle[FIRST_TIME_CONFIGURATION_MODEL] = it
//                    savedStateHandle..putParcelable("key", myObject);


                    delay(5000L)


                    if (it != iconRepository.firsttimelist[iconRepository.firsttimelist.size - 1]) {

                        _bouncingCommand.value = false
//


//                        savedStateHandle[BOUNCING_COMMAND] = false

                        _randomColor.value =
                            Color.argb(
                                255, Random.nextInt(0, 256), Random.nextInt(0, 256),
                                Random.nextInt(0, 256)
                            )
//                        savedStateHandle[RANDOM_COLOR] = Color.argb(
//                            255, Random.nextInt(0, 256), Random.nextInt(0, 256),
//                            Random.nextInt(0, 256)
//                        )

                    } else {

                        _isAnimstart.value = true

                        _isHide.value = false


//                        savedStateHandle[IS_ANIMATE_START] = true
//                        savedStateHandle[IS_HIDE] = false

                    }





                    delay(5000L)
                }
//            to(
//
//                gotoHomeScreenEvent()
//
//            )

            withContext(Dispatchers.Main)
            {


                gotoHomeScreenEvent()

            }


        }

    }


    fun gotoHomeScreenEvent() {
        viewModelScope.launch {

            preferencesDataStore.editFirsttimerun()

            firstconfigtohomeChannel.send(FirstConfigEvent.NavigatefromFirstConfigtoHome)


        }


    }

    fun onPauseEvent() {

        savedStateHandle[BOUNCING_COMMAND] = _bouncingCommand.value
        savedStateHandle[RANDOM_COLOR] = _randomColor.value
        savedStateHandle[FIRST_TIME_CONFIGURATION_MODEL_ICON_NAME] = _firsttimeConfigurationModel.value.iconName
        savedStateHandle[FIRST_TIME_CONFIGURATION_MODEL_ICON] = _firsttimeConfigurationModel.value.icon
        savedStateHandle[IS_HIDE] = _isHide.value
        savedStateHandle[IS_ANIMATE_START] = _isAnimstart.value


    }

    fun onResumeEvent() {


        viewModelScope.launch {



            if (_firsttimeConfigurationModel.value != iconRepository.firsttimelist[iconRepository.firsttimelist.size - 1]

            ) {

                _bouncingCommand.value = false
//


//                        savedStateHandle[BOUNCING_COMMAND] = false

                _randomColor.value =
                    Color.argb(
                        255, Random.nextInt(0, 256), Random.nextInt(0, 256),
                        Random.nextInt(0, 256)
                    )
//                        savedStateHandle[RANDOM_COLOR] = Color.argb(
//                            255, Random.nextInt(0, 256), Random.nextInt(0, 256),
//                            Random.nextInt(0, 256)
//                        )


            } else {

                _isAnimstart.value = true

                _isHide.value = false


//                        savedStateHandle[IS_ANIMATE_START] = true
//                        savedStateHandle[IS_HIDE] = false

                delay(5000L)

                gotoHomeScreenEvent()

            }


        }



    }




    sealed class FirstConfigEvent {
        object NavigatefromFirstConfigtoHome : FirstConfigEvent()

    }


}