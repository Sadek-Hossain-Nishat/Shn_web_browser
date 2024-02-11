package com.shn.company.limited.shnwebbrowserapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class PermissionViewModel
@AssistedInject
constructor
    (
    @ApplicationContext val context: Context,
    @Assisted val permissions:Array<String>
) : ViewModel() {


    val permissionEventChannel = Channel<PermissionEvent> ()

    val permissionEvents = permissionEventChannel.receiveAsFlow().flowOn(Dispatchers.Main)



    // It's a factory of this viewmodel, we need
    // to annotate this factory interface
    // with @AssistedFactory in order to
    // let Dagger-Hilt know about it
    @AssistedFactory
    interface PermissonViewModelFactory {
        fun create(permissons:Array<String>): PermissionViewModel
    }

    // Suppressing unchecked cast warning
    @Suppress("UNCHECKED_CAST")
    companion object {

        // putting this function inside
        // companion object so that we can
        // access it from outside i.e. from fragment/activity
        fun providesFactory(
            permissonViewModelFactory: PermissonViewModelFactory,
            permissons: Array<String>
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                // using our ArticlesFeedViewModelFactory's create function
                // to actually create our viewmodel instance
                return permissonViewModelFactory.create(permissons) as T
            }
        }
    }

    private var permissionOkResult = true




    init {





        viewModelScope.launch {


            when {


                /***

                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION

                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED-> {
                    permissionEventChannel.send(PermissionEvent.PermissionYes)
                    // You can use the API that requires the permission.
                }
                ***/
                permissionCallback() ->{

                    permissionEventChannel.send(PermissionEvent.PermissionYes)



                }

                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    /***
                    locationPermissionRequest.launch(
                    arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    )

                     ***/
                    permissionEventChannel.send(PermissionEvent.PermissionNo)
                }
            }



        }

    }


    fun permissionCallback():Boolean {

        for (permission in permissions) {
            permissionOkResult =  ContextCompat.checkSelfPermission(
                context,
                permission

            ) == PackageManager.PERMISSION_GRANTED && permissionOkResult
        }
        return  permissionOkResult



    }


    sealed class PermissionEvent{
        object PermissionYes:PermissionEvent()
        object PermissionNo:PermissionEvent()
    }


}
