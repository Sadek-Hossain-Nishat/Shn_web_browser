package com.shn.company.limited.shnwebbrowserapp.ui.firsttimeconfiguration

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter

//@BindingAdapter("randomTextColor")
//fun setRandomTextcolor(textView: TextView, color:String) {
//  textView.setTextColor(Color.parseColor(color))
//
//}
@BindingAdapter("randomTextColor")
fun setRandomTextcolor(textView: TextView, color:Int) {
  textView.setTextColor(color)

}



@BindingAdapter("randomImageColor")
fun setRandomTextcolor(imageView: ImageView, color:Int) {
  imageView.setColorFilter(color)

}


