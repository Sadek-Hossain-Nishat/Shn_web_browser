package com.shn.company.limited.shnwebbrowserapp.ui.splash

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.shn.company.limited.shnwebbrowserapp.R
import com.shn.company.limited.shnwebbrowserapp.databinding.FragmentSplashBinding


import com.shn.company.limited.shnwebbrowserapp.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashFragment: Fragment(R.layout.fragment_splash) {

    private var _binding: FragmentSplashBinding?=null

    private  val binding get() = _binding

 

    private val splashViewModel:SplashViewModel by viewModels()




    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment


        requireActivity().window.statusBarColor = Color.TRANSPARENT

        _binding = DataBindingUtil.bind(view)


        binding?.lifecycleOwner= this

        binding?.splashviewmodel =splashViewModel


        /***
        repeatOnLifecycle restarts its coroutine from scratch on each repeat,
        and cancels it each time lifecycle falls below the specified state.
        It’s a natural fit for collecting most flows,
        because it fully cancels the flow when it’s not needed,
        which saves resources related to the flow continuing to emit values.

        ***/


//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//
//                goToRecipesFragment()
//
//            }
//        }
//

        /***
        launchWhenX doesn’t cancel the coroutine and restart it.
        It just postpones when it starts,
        and pauses execution while below the specified state.
        They plan to deprecate these functions but I suspect
        there will need to be some replacement if they do,
        for the case where you are calling some time consuming
        suspend function and then want to do something when it’s done,
        like starting a fragment transaction.
        Using repeatOnLifecycle for this would result in redoing the time consuming action.


        ***/

        // in this section it is good practice to use launchWhenCreated





        lifecycleScope.launchWhenCreated {

            goToRecipesFragment()



        }












    }

    private suspend fun goToRecipesFragment() {

       /*** while (true) {
            delay(1000)
            if (progressing < 100) {
                binding?.progressBar?.progress  = progressing
                progressing += 30


            } else {

                val action = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                findNavController().navigate(action)

            }
        }

       ***/

        splashViewModel.splashEvent.collect{
            event ->
            when (event) {
                SplashViewModel.SplashEvent.NavigateFromSplashToFirstConfigScreen -> {
                    requireActivity().window.statusBarColor =Color.TRANSPARENT

                    val action = SplashFragmentDirections.actionSplashFragmentToFirstConfigurationFragment()
                    findNavController().navigate(action)

                }

                SplashViewModel.SplashEvent.NavigateFromSplashToHomeScreen -> {
                    requireActivity().window.statusBarColor =Color.TRANSPARENT
                    val action = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
                    findNavController().navigate(action)
                }
            }.exhaustive
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
//        (activity as AppCompatActivity?)!!.supportActionBar!!.show()




    }

    override fun onPause() {
        super.onPause()
        Log.d("debugval", "onPause: called ")
        splashViewModel.onPauseEvent()
    }

    override fun onResume() {
        super.onResume()
        Log.d("debugval", "onResume: called ")

        splashViewModel.onResumeEvent()
    }






}