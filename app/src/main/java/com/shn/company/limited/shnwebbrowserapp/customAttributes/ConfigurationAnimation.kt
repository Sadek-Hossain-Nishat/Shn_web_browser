package com.shn.company.limited.shnwebbrowserapp.ui.firsttimeconfiguration

import android.os.SystemClock
import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.BindingAdapter
import com.shn.company.limited.shnwebbrowserapp.R
import kotlinx.coroutines.delay


@BindingAdapter("bouncingAnimation")
fun setbouncingAnimation(view: View, yes:Boolean){

    if (yes) {

        view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.zoom_in_fade_in))



    } else {





        view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.zoom_out_fade_out))

    }




}
