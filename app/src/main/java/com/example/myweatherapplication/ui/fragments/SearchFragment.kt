package com.example.myweatherapplication.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapplication.*
import com.example.myweatherapplication.R
import com.example.myweatherapplication.adapters.LocationAdapter
import com.example.myweatherapplication.databinding.FragmentSearchBinding
import com.example.myweatherapplication.models.CityTown
import com.example.myweatherapplication.util.Constants
import com.example.myweatherapplication.viewmodels.WeatherViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(),EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private lateinit var binding:FragmentSearchBinding
    private lateinit var placeAdapter: LocationAdapter
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val PERMISSION_ID = 1010
    private var current_location:String? = null
    private var currentCity: CityTown? = null

    @Inject
    lateinit var sharedPref: SharedPreferences


 var citiesList  = mutableListOf<CityTown>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
              // activity!!.onBackPressed()
            }

        })


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_search, container, false)
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarExercise)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        (activity as AppCompatActivity).supportActionBar?.title = "Manage Location"
       (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
          binding.toolbarExercise.setNavigationOnClickListener {
             //requireActivity().onBackPressed()
              findNavController().navigateUp()

          }



        showCurrentLocation()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
          setUpRecyclerView()

      //  val crnt_location_first = sharedPref.getBoolean(Constants.CURRENT_LOCATION_FIRST_TIME,true)

        if (isNetworkAvailable(requireContext())) {


            getPlace()
            //requestEasyPermissions()
            // hasLocationsPermissions()
        } else {
            showRationalDialogForInternetPermissions()
        }



        viewModel.getLocations().observe(viewLifecycleOwner) {
            it?.let { locationsList ->
                citiesList.clear()
                citiesList.addAll(locationsList)
                placeAdapter.differ.submitList(citiesList)
            }
        }

        binding.crntLocationBtn.setOnClickListener {
          //  val loc = sharedPref.getBoolean(Constants.CURRENT_LOCATION,false)
          //  if(!loc){
               getLastLocation()
         //   binding.crntLocationBtn.visibility = View.GONE

          //  sharedPref.edit().putBoolean(Constants.CURRENT_LOCATION,true).apply()
        }


    }
    private fun showCurrentLocation(){
        val loc = sharedPref.getBoolean(Constants.CURRENT_LOCATION,false)
        if(!loc){
            binding.crntLocationBtn.visibility = View.VISIBLE
        }else{
            binding.crntLocationBtn.visibility = View.GONE
        }
    }





    private fun setUpRecyclerView(){
        placeAdapter = LocationAdapter(this)
        binding.rvLocations.apply {
            adapter = placeAdapter
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
        }
    }


     fun setOnClick(city: CityTown){

         val bundle = Bundle().apply {
             putString("city",city.name)
         }
         if(findNavController().currentDestination?.id == R.id.searchFragment)
             sharedPref.edit().putString(Constants.LOC_NAME,city.name).apply()
        findNavController().navigate(R.id.action_searchFragment_to_mainFragment,bundle)
        // getWeather(city.name)
    }

    private fun getPlace(){

      //  val list = ArrayList<CityTown>()

            autocompleteFragment =
                childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment



        context?.let { Places.initialize(it, resources.getString(R.string.api_key)) }

        //autocompleteFragment.setText("Add location")
            //  autocompleteFragment.setText("London,UK")
            //  getWeather("london")



            autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))
            // Set the desired autocomplete activity mode
            autocompleteFragment.setActivityMode(AutocompleteActivityMode.FULLSCREEN)


            autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {


                override fun onPlaceSelected(place: Place) {


                    val placeName = place.name
                 //   val placeAddress = place.address
                    val city = CityTown(placeName!!)



                    // list.add(city)
                    viewModel.insertLocation(city)

                    //   getWeather(placeName)

                }

                override fun onError(p0: Status) {

                }

            })

    }

    @SuppressLint("MissingPermission", "SetTextI18n", "SuspiciousIndentation")
    private fun getLastLocation() {

        if (CheckPermission()) {
            if (isLocationEnabled(requireContext())) {


                fusedLocationProviderClient.lastLocation.addOnCompleteListener { it ->
                    val location: Location? = it.result
                    if (location == null) {
                        newLocationData()
                    } else {
                        //  binding.setText(getCityName(location.latitude, location.longitude))
                       // viewModel.getResponse(getCityName(location.latitude, location.longitude))
                        //  getWeath
                            //  er()
                        current_location = getCityName(location.latitude,location.longitude)
                          currentCity = CityTown(getCityName(location.latitude,location.longitude))
                        sharedPref.edit().putString(Constants.CURNT_LOC_NAME, current_location).apply()

                     //   var isPresent:Boolean = false
                  //    viewModel.getLocations().observe(viewLifecycleOwner, Observer {
                      //    if(it.isEmpty()){
                             // if(binding.crntLocationBtn.visibility == View.INVISIBLE) {
                              val cl = sharedPref.getBoolean(Constants.CURRENT_LOCATION,false)
                             if(!cl) {
                                  viewModel.insertLocation(currentCity!!)
                                 viewModel.getLocations().observe(viewLifecycleOwner) {
                                     it?.let { locationsList ->
                                         citiesList.clear()
                                         citiesList.addAll(locationsList)
                                         placeAdapter.differ.submitList(citiesList)
                                         placeAdapter.notifyDataSetChanged()

                                     }
                                 }
                                 binding.crntLocationBtn.visibility = View.GONE
                                 sharedPref.edit().putBoolean(Constants.CURRENT_LOCATION,true).apply()

                              }

                        sharedPref.edit().putBoolean(Constants.SWITCH_TOGGLE,true).apply()
                    }
                }


            } else {
                showRationalDialogForLocationPermissions()

            }
        } else {
            // findNavController().navigate(R.id.action_mainFragment_to_searchFragment)

            requestPermission()

        }
    }


    @SuppressLint("MissingPermission")
    fun newLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        Looper.myLooper()?.let {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, it
            )
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location? = locationResult.lastLocation
            // binding.editText.setText(getCityName(lastLocation.latitude,lastLocation.longitude))
            viewModel.getResponse(getCityName(lastLocation!!.latitude, lastLocation.longitude))
            //   getWeather()
        }
    }

    private fun CheckPermission(): Boolean {

        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }

        return false

    }

    private fun requestPermission() {



        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(context: Context): Boolean {

        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }





    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)

        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //    val ss = sharedPref.getBoolean(Constants.CURRENT_LOCATION_FIRST_TIME,true)
                getLastLocation()
             //   sharedPref.edit().putBoolean(Constants.CURRENT_LOCATION,true).apply()

            }else {
                binding.crntLocationBtn.visibility = View.VISIBLE
                sharedPref.edit().putBoolean(Constants.CURRENT_LOCATION,false).apply()
                // findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
            }
        }
    }
  /*  private fun leaveFragment(){
        AlertDialog.Builder(requireActivity()).setMessage(
            "Do you want to leave the app?"
        )
            .setPositiveButton("Yes")
            { _, _ ->

            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                //  findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
            }.show()
    }*/




    private fun getCityName(lat: Double, long: Double): String {

        var cityName = ""
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val Address = geoCoder.getFromLocation(lat, long, 3)

        cityName = Address.get(0).locality


        return cityName
    }





    private fun isNetworkAvailable(context: Context?): Boolean {

        binding.ivNoInternet.visibility = View.INVISIBLE
        binding.tvNoInternet.visibility = View.INVISIBLE
        binding.rvLocations.visibility = View.VISIBLE
        binding.clAuto.visibility = View.VISIBLE


        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    private fun showRationalDialogForLocationPermissions() {
        AlertDialog.Builder(requireActivity()).setMessage(
            "It looks like you turned off your location permissions." +
                    "It can be enabled under Application Settings"
        )
            .setPositiveButton("GO TO SETTINGS")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(intent, LOCATION_SETTINGS)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                //  findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
            }.show()
    }

    private fun showRationalDialogForInternetPermissions() {
        binding.ivNoInternet.visibility = View.VISIBLE
        binding.tvNoInternet.visibility = View.VISIBLE
        binding.rvLocations.visibility = View.INVISIBLE
        binding.clAuto.visibility = View.INVISIBLE


        AlertDialog.Builder(requireActivity()).setMessage(
            "Check your network." +
                    "It can be enabled under Application Settings"
        )
            .setPositiveButton("GO TO SETTINGS")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivityForResult(intent, WIFI_SETTINGS)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
               // findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
            }.show()

    }




     fun deleteLocation(city: CityTown, position: Int){
val name = sharedPref.getString(Constants.CURNT_LOC_NAME,"")
         if(city.name == name){

             viewModel.deleteLocation(city)

             citiesList.removeAt(position)

          //   placeAdapter.differ.submitList(citiesList)
             placeAdapter.deleteItem(position)
             Toast.makeText(activity,"Current location deleted",Toast.LENGTH_LONG).show()

             //  viewModel.deleteLocation(city)
             binding.crntLocationBtn.visibility = View.VISIBLE
             sharedPref.edit().putBoolean(Constants.CURRENT_LOCATION,false).apply()
//ss()
         }else {

             viewModel.deleteLocation(city)

             citiesList.removeAt(position)
           //  placeAdapter.deleteItem(position)
            ///    placeAdapter.differ.submitList(citiesList)
              placeAdapter.notifyDataSetChanged()

         }



// Submit the updated list to the adapter



       // ss()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == WIFI_SETTINGS  || requestCode == LOCATION_SETTINGS) {
           // val ss = sharedPref.getBoolean(Constants.CURRENT_LOCATION_FIRST_TIME,true)

            getLastLocation()
            getPlace()
            binding.ivNoInternet.visibility = View.INVISIBLE
            binding.tvNoInternet.visibility = View.INVISIBLE
            binding.rvLocations.visibility = View.VISIBLE
            binding.clAuto.visibility = View.VISIBLE

        }
    }



    @SuppressLint("MissingPermission")
    private fun requestEasyPermissions() {

        if (Constants.hasLocationPermissions(requireContext())) {
            if (isLocationEnabled(requireContext())) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                    val location: Location? = it.result
                    if (location == null) {
                        newLocationData()
                    } else {
                        //  binding.setText(getCityName(location.latitude, location.longitude))
                        viewModel.getResponse(
                            getCityName(
                                location.latitude,
                                location.longitude
                            )
                        )
                        //  getWeather()
                    }
                }


            } else {
                showRationalDialogForLocationPermissions()

            }

        } else {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions to use this app",
                    Constants.REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {


    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestEasyPermissions()
        }
    }
    companion object {
       // const val WI_FI = "1"
        const val LOCATION_SETTINGS = 2
        const val WIFI_SETTINGS = 3
    }

    override fun onClick(v: View?) {
      //  setOnClick(CityTown(current_location))
    }

}

