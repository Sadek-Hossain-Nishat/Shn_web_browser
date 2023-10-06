package com.shn.company.limited.shnwebbrowserapp.ui.home

data class LocationModel (val latitude:Double=1.0,
                          val longitude:Double=2.0 ){
    companion object{

        val defaultInstance:LocationModel by lazy {
            LocationModel()
        }


    }
}