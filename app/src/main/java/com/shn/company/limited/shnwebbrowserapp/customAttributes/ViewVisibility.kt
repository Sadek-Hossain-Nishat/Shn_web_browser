package com.shn.company.limited.shnwebbrowserapp.customAttributes

import android.opengl.Visibility
import android.view.View
import androidx.databinding.BindingAdapter


@BindingAdapter("hideView")
fun setHideView(view: View, hide: Boolean) {

   view.visibility = if (hide) View.GONE else View.VISIBLE



}