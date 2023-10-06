package com.shn.company.limited.shnwebbrowserapp.ui.home


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat


import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar


import com.shn.company.limited.shnwebbrowserapp.R
import com.shn.company.limited.shnwebbrowserapp.databinding.FragmentHomeBinding
import com.shn.company.limited.shnwebbrowserapp.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private var _binding: FragmentHomeBinding?=null

    private val binding get() = _binding

    private val homeviewmodel:HomeViewModel by viewModels()







    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions->
            when{
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION,false)->{
                    // Precise location access granted.
                    Toast.makeText(requireContext(),"Precise location access granted",Toast.LENGTH_LONG).show()
                    createLocationRequest()

                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false)->{
                    // Only approximate location access granted.
                    Toast.makeText(requireContext(),"Only approximate location access granted",Toast.LENGTH_LONG).show()

                }
                else->{
                    // No location access granted.
                    Toast.makeText(requireContext(),"No location access granted",Toast.LENGTH_LONG).show()
                }

            }


        }

    fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }





        val builder = locationRequest?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = builder?.let { client.checkLocationSettings(it.build()) } as Task<LocationSettingsResponse>

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            if (locationSettingsResponse.locationSettingsStates?.isLocationPresent == true){
                Toast.makeText(requireContext(),"Location settings is  already enabled",Toast.LENGTH_LONG).show()
                findLoaction()
            }
            else Toast.makeText(requireContext(),"Location settings has been enabled",Toast.LENGTH_LONG).show()



        }

        task.addOnFailureListener { exception ->


            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.

                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().

//                    exception.startResolutionForResult(requireActivity(),
//                        200)
                    startIntentSenderForResult(exception.resolution.intentSender,200, null, 0, 0, 0, null);



                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("tag", "onActivityResult: Got")

        if(requestCode == 200){

            Toast.makeText(requireContext(),"Desired Request got",Toast.LENGTH_LONG).show()

            if (resultCode == RESULT_OK) {
                // so some work
                Toast.makeText(requireContext(),"Location settings is enabled",Toast.LENGTH_LONG).show()
                findLoaction()
            }else{
                Toast.makeText(requireContext(),"Location settings is not enabled",Toast.LENGTH_LONG).show()
            }

        }

    }



    @SuppressLint("MissingPermission")
    private fun findLoaction() {


        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener {
            location:Location?->
            if (location != null) {

                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addressess = geocoder.getFromLocation(location.latitude,location.longitude,
                    1)

                val city = addressess!![0].locality
                val addresline = addressess!![0].getAddressLine(0)




                val countryname = addressess!![0].countryName



//                Toast.makeText(requireContext(),"Location ${location.latitude} ${location.longitude}",Toast.LENGTH_LONG).show()
//                Toast.makeText(requireContext(),"Location ${addresline}",Toast.LENGTH_LONG).show()
                Toast.makeText(requireContext(),"Location ${addressess}",Toast.LENGTH_LONG).show()


            }else{
                Toast.makeText(requireContext(),"Location not found",Toast.LENGTH_LONG).show()
            }


        }



    }
// find the current place
//    https://developer.android.com/training/location/retrieve-current

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationandInternetruntimepermissionCheck()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun locationandInternetruntimepermissionCheck() {
        when {

            ContextCompat.checkSelfPermission(
                requireContext(),
              Manifest.permission.ACCESS_FINE_LOCATION

            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION) ==PackageManager.PERMISSION_GRANTED-> {
                createLocationRequest()
                // You can use the API that requires the permission.
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
               locationPermissionRequest.launch(
                   arrayOf(
                       Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.ACCESS_COARSE_LOCATION
                   )
               )
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DataBindingUtil.bind(view)

        binding?.lifecycleOwner = this
        binding?.homeviewmodel = homeviewmodel


        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(),R.color.colorPrimary)

        lifecycleScope.launch{
            internetStatusSignal()
        }


    }








    private suspend fun internetStatusSignal() {
//        homeviewmodel.homepageEvents.collect{
//            event ->
//
//            when(event){
//                HomeViewModel.HomepageEvent.InternetstatusnoEvent -> {
//
//                    Snackbar.make(requireView(),"No Internet Connection",Snackbar.LENGTH_SHORT).show()
//
//
//
//
//                }
//                HomeViewModel.HomepageEvent.InternetstatusyesEvent -> {
//                    Log.i("internetstatuscheck", "Everything is okey ")
//
//                }
//
//                HomeViewModel.HomepageEvent.InternetstatusyesafternoeventinAppRuntime -> {
//
//                    Snackbar.make(requireView(),"Online Now",Snackbar.LENGTH_SHORT).show()
//
//                }
//            }.exhaustive
//        }



        homeviewmodel.share.collect{

            when (it) {
                HomeViewModel.NetworkStatusState.NetworkStatusConnected -> {

                    Snackbar.make(requireView(),"Online",Snackbar.LENGTH_SHORT).show()



                }
                HomeViewModel.NetworkStatusState.NetworkStatusDisconnected -> {

                    Snackbar.make(requireView(),"No Internet Connection",Snackbar.LENGTH_SHORT).show()

                }
            }.exhaustive

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i("lifecycle", "onDestroy: called ")


    }

    override fun onStart() {
        super.onStart()
        Log.i("lifecycle", "onStart: called ")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("lifecycle", "onAttach: called ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("lifecycle", "onDetach:called ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("lifecycle", "onDestroyView:called ")
        homeviewmodel.editInterStatus()
    }


}