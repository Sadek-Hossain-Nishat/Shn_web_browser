package com.shn.company.limited.shnwebbrowserapp.network

import android.content.Context
import android.net.ConnectivityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class CheckInternetConnection @Inject constructor(@ApplicationContext val context:Context) {

    fun internetStatus(): Boolean {

        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo

        return nInfo != null && nInfo.isConnectedOrConnecting

    }
}