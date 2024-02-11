package com.shn.company.limited.shnwebbrowserapp.ui.firsttimeconfiguration

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.shn.company.limited.shnwebbrowserapp.R
import com.shn.company.limited.shnwebbrowserapp.databinding.FragmentFirstconfigurationBinding
import com.shn.company.limited.shnwebbrowserapp.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FirstConfigurationFragment:Fragment(R.layout.fragment_firstconfiguration) {

    private var _binding:FragmentFirstconfigurationBinding? = null

    private val binding get() = _binding

    private val configurationModel:ConfigurationViewModel by viewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = DataBindingUtil.bind(view)


        binding?.lifecycleOwner = this

        binding?.firstconfigurationviewmodel = configurationModel


        lifecycleScope.launchWhenCreated {

            navigatefromFirstConfigtoHomeScreen()
        }




    }

    private suspend fun navigatefromFirstConfigtoHomeScreen() {
        configurationModel.firstconfigtoHomeEvent.collect{
            event ->

            when(event) {
                ConfigurationViewModel.FirstConfigEvent.NavigatefromFirstConfigtoHome -> {
                    val action = FirstConfigurationFragmentDirections.actionFirstConfigurationFragmentToHomeFragment()
                    findNavController().navigate(action)
                }



            }.exhaustive


        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onPause() {
        super.onPause()
        configurationModel.onPauseEvent()
    }

    override fun onResume() {
        super.onResume()
        configurationModel.onResumeEvent()
    }



}