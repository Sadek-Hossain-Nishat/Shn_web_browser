package com.shn.company.limited.shnwebbrowserapp.ui.firsttimeconfiguration

import android.graphics.Color
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
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val iconRepository: IconRepository,
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel(

) {


    // livedata and stateflow for single data they act similar so
    // we can use stateflow insted of livedata
    // because stateflow is latest features in modern android development


    private var _bouncingCommand = MutableStateFlow<Boolean>(false)


    val bouncingCommand: StateFlow<Boolean> = _bouncingCommand
//   private var _randomColor =MutableStateFlow<String>(
//       java.lang.String.format(
//           "#%06X",
//           0xFFFFFF and Color.argb(255,Random.nextInt(0,256),Random.nextInt(0,256),
//           Random.nextInt(0,256))
//       ))

    private var _randomColor = MutableStateFlow<Int>(
        Color.argb(
            255, Random.nextInt(0, 256), Random.nextInt(0, 256),
            Random.nextInt(0, 256)
        )
    )


    val randromColor: StateFlow<Int> = _randomColor

    private var _firsttimeConfigurationModel =
        MutableStateFlow<FirstTimeConfigurationModel>(FirstTimeConfigurationModel("", 0))


    val firstTimeConfigurationModel: StateFlow<FirstTimeConfigurationModel> =
        _firsttimeConfigurationModel


    private var _isHide = MutableStateFlow<Boolean>(true)


    val isHide: StateFlow<Boolean> = _isHide


    private var _isAnimstart = MutableStateFlow<Boolean>(false)

    val isAnimstart: StateFlow<Boolean> = _isAnimstart


    private val firstconfigtohomeChannel = Channel<FirstConfigEvent>()

    val firstconfigtoHomeEvent = firstconfigtohomeChannel.receiveAsFlow().flowOn(Dispatchers.Main)


    init {

        viewModelScope.launch {




                        flow<FirstTimeConfigurationModel> {

                            iconRepository.firsttimelist.forEach {

                                delay(1000L)


                                emit(it)


                            }
                        }.flowOn(Dispatchers.Default)
                            .collect {


                                _randomColor.value =
                                    Color.argb(
                                        255, Random.nextInt(0, 256), Random.nextInt(0, 256),
                                        Random.nextInt(0, 256)
                                    )
                                _bouncingCommand.value = true
                                _firsttimeConfigurationModel.value = it
                                delay(5000L)


                                if (it != iconRepository.firsttimelist[iconRepository.firsttimelist.size - 1]) {

                                    _bouncingCommand.value = false

                                    _randomColor.value =
                                        Color.argb(
                                            255, Random.nextInt(0, 256), Random.nextInt(0, 256),
                                            Random.nextInt(0, 256)
                                        )

                                } else {

                                    _isAnimstart.value = true

                                    _isHide.value = false

                                }





                                delay(5000L)
                            }
                        to(
                            gotoHomeScreenEvent()
                        )





                    }

                }









fun gotoHomeScreenEvent() {
    viewModelScope.launch {

        preferencesDataStore.editFirsttimerun()

        firstconfigtohomeChannel.send(FirstConfigEvent.NavigatefromFirstConfigtoHome)


    }


}


sealed class FirstConfigEvent {
    object NavigatefromFirstConfigtoHome : FirstConfigEvent()

}


}