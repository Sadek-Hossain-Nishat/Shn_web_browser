package com.shn.company.limited.shnwebbrowserapp.ui.firsttimeconfiguration

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.shn.company.limited.shnwebbrowserapp.R


@BindingAdapter("randomText")
fun setRandomText(textView: TextView, text: String) {

    textView.text = text

}

@BindingAdapter("randomImage")
fun setRandomText(imageView: ImageView,imageResource:Int) {

   imageView.setImageResource(imageResource)

}