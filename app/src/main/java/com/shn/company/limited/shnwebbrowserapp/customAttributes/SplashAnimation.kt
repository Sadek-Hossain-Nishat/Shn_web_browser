package com.shn.company.limited.shnwebbrowserapp.ui.splash

import android.view.View
import android.view.animation.AnimationUtils

import androidx.databinding.BindingAdapter
import com.shn.company.limited.shnwebbrowserapp.R





        @BindingAdapter("startAnimation")
        fun setstartingAnimation(view: View, willStart:Boolean){

            if (willStart) {
                view.startAnimation(AnimationUtils.loadAnimation(view.context, R.anim.zoom_in_fade_in))

            }




        }








