package com.shn.company.limited.shnwebbrowserapp.ui.firsttimeconfiguration

import android.content.Context
import com.shn.company.limited.shnwebbrowserapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject



class IconRepository @Inject constructor(
    @ApplicationContext context:Context
) {

//     private val stringArray = listOf(
//
//         "Welcome to SHN WEB",
//    "Why you will use SHN WEB?",
//    "There are some reasons",
//    "Secure web browsing",
//    "Desirable runtime permission",
//    "Get current location",
//    "Excellent downloading features",
//    "Nice media viewer system",
//    "Get all needed featurs",
//    "Now is everything OK"
//
//     )


    private val stringArray = context.resources.getStringArray(R.array.first_time_configuration);






     private val iconarray = listOf(
        R.drawable.ic_welcome,
        R.drawable.ic_info,
        R.drawable.ic_reasons,
        R.drawable.ic_webbrowser,
        R.drawable.ic_permission,
        R.drawable.ic_location,
        R.drawable.ic_download,
        R.drawable.ic_mediaviewer,
        R.drawable.ic_neededfeatures,
        R.drawable.ic_okay,




    )



       val firsttimelist = iconarray.mapIndexed { index, i ->



           FirstTimeConfigurationModel(stringArray[index],i)
       }






    


















}