package com.example.myweatherapplication.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myweatherapplication.*
import com.example.myweatherapplication.R
import com.example.myweatherapplication.data.weather.WeatherResponse
import com.example.myweatherapplication.databinding.FragmentMainBinding
import com.example.myweatherapplication.util.Constants
import com.example.myweatherapplication.util.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.myweatherapplication.util.ScreenState
import com.example.myweatherapplication.viewmodels.WeatherViewModel
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.swipe_layout
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(),EasyPermissions.PermissionCallbacks,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var fragment: TodayFragment
    val args:MainFragmentArgs by navArgs()


    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val PERMISSION_ID = 1010



    @Inject
    lateinit var sharedPref: SharedPreferences






    var dialogView:View? = null
    var dialogViewWind:View? = null
    var dialogViewPrecipitation:View? = null
    var dialogViewPressure:View? = null
    var dialogViewVisibility:View? = null
    var radioGroup:RadioGroup? = null
    var radioGroupWind:RadioGroup? = null
    var radioGroupPrecipitation:RadioGroup? = null
    var radioGroupPressure:RadioGroup? = null
    var radioGroupVisibility:RadioGroup? = null
    var celciusBtn:RadioButton? = null
    var farenheitBtn:RadioButton? = null
    var kmBtn:RadioButton? = null
    var mphBtn:RadioButton? = null
    var degreeBtn:RadioButton? = null
    var dirBtn:RadioButton? = null
    var pressureInBtn:RadioButton? = null
    var pressureMbBtn:RadioButton? = null
    var preMmBtn:RadioButton? = null
    var preInBtn:RadioButton? = null
    var visKmBtn:RadioButton? = null
    var visMilesBtn:RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
              leaveFragment()
            }

        })

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("VisibleForTests", "MissingPermission", "SuspiciousIndentation",
        "ClickableViewAccessibility", "InflateParams", "LogNotTimber"
    )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment = TodayFragment()

     val city = sharedPref.getString(Constants.LOC_NAME,"")
        binding.locationName.text = city


        binding.navView.setNavigationItemSelectedListener(this)


        val toggle = ActionBarDrawerToggle(activity,binding.swipeLayout,R.string.open_nav,R.string.close_nav)
             binding.swipeLayout.addDrawerListener(toggle)
        toggle.syncState()
        // viewModel = (activity as MainActivity).viewModel
          binding.settingBtn.setOnClickListener {
              binding.swipeLayout.openDrawer(GravityCompat.START)
          }
        swipe_layout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (swipe_layout.isDrawerOpen(GravityCompat.START)) {
                    val outRect = IntArray(2)
                    swipe_layout.getLocationOnScreen(outRect)
                    if (event.rawX < outRect[0] || event.rawX > outRect[0] + swipe_layout.width ||
                        event.rawY < outRect[1] || event.rawY > outRect[1] + swipe_layout.height
                    ) {
                        swipe_layout.closeDrawer(GravityCompat.START)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }



    //    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setUpTabBar()

        val name = args.city
        getWeather(name!!)

    /*    if (isNetworkAvailable(requireContext())) {
            //  getLastLocation()
          //  getPlace()
            //requestEasyPermissions()
           // hasLocationsPermissions()
        } else {
          //  showRationalDialogForInternetPermissions()
        }*/

        viewModel.result.observe(viewLifecycleOwner) {
            it?.let { response ->
                when (response) {
                    is ScreenState.Success -> {

                        setImage(
                            response.data!!.current.condition.code,
                            response.data.current.is_day
                        )
                        Log.i("suctmrw", "{${response.data}}")
                    }

                    is ScreenState.Loading -> {

                    }

                    is ScreenState.Error -> {

                    }
                }
            }
        }


        initializeValuesForTemp()
        initializeValuesForWind()
        initializeValuesForPrecipitation()
        initializeValuesForPressure()
        initializeValuesForVisibility()

    }

   /* private fun getPlace(){
        autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment


        Places.initialize(requireContext(), resources.getString(R.string.api_key))


        //  autocompleteFragment.setText("London,UK")
        //  getWeather("london")

        autocompleteFragment.setPlaceFields(
            listOf(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS)
        )
        // Set the desired autocomplete activity mode
        autocompleteFragment.setActivityMode(AutocompleteActivityMode.FULLSCREEN)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {

            override fun onPlaceSelected(place: Place) {

                showCurrentLocation = false

                val placeName = place.name
                val placeAddress = place.address
                if (placeName != null) {

                    getWeather(placeName)
                }
            }

            override fun onError(p0: Status) {

            }

        })
    }*/

    private  fun setImage(code:Int,isDay:Int){
        when(code){
            in 1001..1007->{
                binding.settingBtn.setBackgroundColor(Color.parseColor("#243B55"))
                binding.llMain.setBackgroundColor(Color.parseColor("#243B55"))
                 binding.smartTab.setBackgroundColor(Color.parseColor("#243B55"))
                val window: Window = requireActivity().window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.parseColor("#243B55")  // Set your desired color here
            }
            in 1008..1010->{
                binding.settingBtn.setBackgroundColor(Color.parseColor("#2c3e50"))
                binding.llMain.setBackgroundColor(Color.parseColor("#2c3e50"))
                binding.smartTab.setBackgroundColor(Color.parseColor("#2c3e50"))
                val window: Window = requireActivity().window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.parseColor("#2c3e50")
            }
            in 1051..1270->{
                binding.settingBtn.setBackgroundColor(Color.parseColor("#141E30"))
                binding.llMain.setBackgroundColor(Color.parseColor("#141E30"))
                binding.smartTab.setBackgroundColor(Color.parseColor("#141E30"))
                val window: Window = requireActivity().window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.parseColor("#141E30")
            }
            1000-> {

                if (isDay == 1) {
                    binding.settingBtn.setBackgroundColor(Color.parseColor("#2948ff"))
                    binding.llMain.setBackgroundColor(Color.parseColor("#2948ff"))
                    binding.smartTab.setBackgroundColor(Color.parseColor("#2948ff"))
                    val window: Window = requireActivity().window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = Color.parseColor("#2948ff")

                }else{
                    binding.settingBtn.setBackgroundColor(Color.parseColor("#3f4c6b"))
                    binding.llMain.setBackgroundColor(Color.parseColor("#3f4c6b"))
                    binding.smartTab.setBackgroundColor(Color.parseColor("#3f4c6b"))
                    val window: Window = requireActivity().window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = Color.parseColor("#3f4c6b")
                }
            }
        }
    }



    private fun setUpTabBar() {

        val adapter = FragmentPagerItemAdapter(
            childFragmentManager,
            FragmentPagerItems.with(activity)
                .add("Today", TodayFragment::class.java)
                .add("Tomorrow", TomorrowFragment::class.java)
                .add("Forecast", ForecastFragment::class.java)
                .create()
        )
        binding.viewPager.adapter = adapter
        binding.smartTab.setViewPager(binding.viewPager)

    }


private fun leaveFragment(){
    AlertDialog.Builder(requireActivity()).setMessage(
        "Do you want to leave the app?"
    )
        .setPositiveButton("Yes")
        { _, _ ->
           requireActivity().finish()
        }.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            //  findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }.show()
}



    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        if (CheckPermission()) {
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
        // findNavController().navigate(R.id.action_mainFragment_to_searchFragment)

          requestPermission()

        }
    }


    @SuppressLint("MissingPermission")
    @Deprecated("Deprecated in Java")
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
            context.getSystemService(LOCATION_SERVICE) as LocationManager
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
               setUpTabBar()
                getLastLocation()


                   }else {
                 // findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
                 }
            }
}




            private fun getCityName(lat: Double, long: Double): String {

                var cityName = ""
                val geoCoder = Geocoder(requireContext(), Locale.getDefault())
                val Address = geoCoder.getFromLocation(lat, long, 3)

                cityName = Address.get(0).locality

                return cityName
            }


            private fun getWeather(text: String) {


              //  var job: kotlinx.coroutines.Job? = null
                //  binding.editText.setText(cities)
              //  var city = "london"
                //  binding.editText.addTextChangedListener { text->
                // job?.cancel()
                // job = MainScope().launch {

                // delay(1000L)
                //   text.let {
                //    if(text.toString().isNotEmpty()){


                viewModel.getResponse(text)
              //  setUps()



                // setUps()
                //  }
                //  }
                //    }
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




            interface updateTodayFragment {
                fun updateFragment(it: WeatherResponse)
            }


            override fun onResume() {
                super.onResume()
                //  getLastLocation()
                  //getPlace()
             //   requestEasyPermissions()


            }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == LOCATION_SETTINGS || requestCode == WIFI_SETTINGS) {
            getLastLocation()
         //   getPlace()
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
                            REQUEST_CODE_LOCATION_PERMISSION,
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
              //  const val WI_FI = "1"
                const val LOCATION_SETTINGS = 2
                const val WIFI_SETTINGS = 3
            }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.manage_location -> {


                findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
            }
            R.id.temperature ->{

                showDialogForTemp()
            }
            R.id.wind ->{
                showDialogForWind()
            }
            R.id.pressure ->{
                showDialogForPressure()
            }
            R.id.precipitation ->{
                showDialogForPrecipitation()
            }
            R.id.visibility ->{
                showDialogForVisibility()
            }


        }
       // binding.swipeLayout.closeDrawer(GravityCompat.START)
       return true
    }




    private fun showDialogForTemp(){


        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Temperature Unit")
        builder.setView(dialogView)


       val parent = dialogView!!.parent as? ViewGroup
        parent?.removeView(dialogView)

        val dialog = builder.create()
        dialog.show()

        radioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId){
                R.id.celcius -> {

                    celciusBtn!!.isChecked = true
                    farenheitBtn!!.isChecked = false
                   viewModel.toggleTemp(true)
                    sharedPref.edit().putBoolean(Constants.TEMP_UNIT,true).apply()
                }
                R.id.farenheit ->{

                    celciusBtn!!.isChecked = false
                    farenheitBtn!!.isChecked = true
                    viewModel.toggleTemp(false)
                    sharedPref.edit().putBoolean(Constants.TEMP_UNIT,false).apply()
                }

            }
            dialog.dismiss()

        }

    }

    @SuppressLint("InflateParams")
    private fun initializeValuesForTemp(){
        val inflater: LayoutInflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_temp, null)


        radioGroup = dialogView!!.findViewById<RadioGroup>(R.id.radioGroup)
        celciusBtn = radioGroup!!.findViewById<RadioButton>(R.id.celcius)
        farenheitBtn = radioGroup!!.findViewById<RadioButton>(R.id.farenheit)
        celciusBtn!!.isChecked = true
        viewModel.toggleTemp(true)
    }

    @SuppressLint("InflateParams")
    private fun initializeValuesForWind(){
        val inflater: LayoutInflater = layoutInflater
        dialogViewWind = inflater.inflate(R.layout.dialog_wind, null)


        radioGroupWind= dialogViewWind!!.findViewById<RadioGroup>(R.id.radioGroup)
        kmBtn = radioGroupWind!!.findViewById<RadioButton>(R.id.km)
        mphBtn = radioGroupWind!!.findViewById<RadioButton>(R.id.mph)
        degreeBtn = radioGroupWind!!.findViewById<RadioButton>(R.id.degree)
        dirBtn = radioGroupWind!!.findViewById<RadioButton>(R.id.dir)
        kmBtn!!.isChecked = true
        viewModel.windValue("km")
    }

    @SuppressLint("InflateParams")
    private fun initializeValuesForPrecipitation(){
        val inflater: LayoutInflater = layoutInflater
        dialogViewPrecipitation = inflater.inflate(R.layout.dialog_precipitation, null)


        radioGroupPrecipitation= dialogViewPrecipitation!!.findViewById<RadioGroup>(R.id.radioGroup)
        preInBtn = radioGroupPrecipitation!!.findViewById<RadioButton>(R.id.preci_in)
        preMmBtn = radioGroupPrecipitation!!.findViewById<RadioButton>(R.id.preci_mm)

        preInBtn!!.isChecked = true
        viewModel.precipitationValue("preci_in")
    }

    @SuppressLint("InflateParams")
    private fun initializeValuesForPressure(){
        val inflater: LayoutInflater = layoutInflater
        dialogViewPressure = inflater.inflate(R.layout.dialog_pressure, null)


        radioGroupPressure= dialogViewPressure!!.findViewById<RadioGroup>(R.id.radioGroup)
        pressureInBtn = radioGroupPressure!!.findViewById<RadioButton>(R.id.pressure_in)
        pressureMbBtn = radioGroupPressure!!.findViewById<RadioButton>(R.id.pressure_mb)

        pressureInBtn!!.isChecked = true
        viewModel.pressureValue("pressure_in")
    }
    @SuppressLint("InflateParams")
    private fun initializeValuesForVisibility(){
        val inflater: LayoutInflater = layoutInflater
        dialogViewVisibility = inflater.inflate(R.layout.dialog_viisbility, null)


        radioGroupVisibility= dialogViewVisibility!!.findViewById<RadioGroup>(R.id.radioGroup)
        visKmBtn = radioGroupVisibility!!.findViewById<RadioButton>(R.id.vis_kms)
        visMilesBtn = radioGroupVisibility!!.findViewById<RadioButton>(R.id.vis_miles)

        visKmBtn!!.isChecked = true
        viewModel.visibilityValue("vis_km")
    }

    private fun showDialogForWind() {


        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Wind Unit")
        builder.setView(dialogViewWind)


        val parent = dialogViewWind!!.parent as? ViewGroup
        parent?.removeView(dialogViewWind)

        val dialog = builder.create()
        dialog.show()

        radioGroupWind!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.km -> {

                    kmBtn!!.isChecked = true
                    mphBtn!!.isChecked = false
                    degreeBtn!!.isChecked = false
                    dirBtn!!.isChecked = false
                    viewModel.windValue("km")
                    sharedPref.edit().putString(Constants.WIND_VALUE, "km").apply()
                }

                R.id.mph -> {

                    kmBtn!!.isChecked = false
                    mphBtn!!.isChecked = true
                    degreeBtn!!.isChecked = false
                    dirBtn!!.isChecked = false
                    viewModel.windValue("mph")
                    sharedPref.edit().putString(Constants.WIND_VALUE, "mph").apply()
                }
                R.id.degree->{
                    kmBtn!!.isChecked = false
                    mphBtn!!.isChecked = false
                    degreeBtn!!.isChecked = true
                    dirBtn!!.isChecked = false
                    viewModel.windValue("degree")
                    sharedPref.edit().putString(Constants.WIND_VALUE, "degree").apply()
                }
                R.id.dir->{
                    kmBtn!!.isChecked = false
                    mphBtn!!.isChecked = false
                    degreeBtn!!.isChecked = false
                    dirBtn!!.isChecked = true
                    viewModel.windValue("dir")
                    sharedPref.edit().putString(Constants.WIND_VALUE, "dir").apply()
                }


            }
            dialog.dismiss()

        }
    }

    private fun showDialogForPrecipitation() {


        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Precipitation Unit")
        builder.setView(dialogViewPrecipitation)


        val parent = dialogViewPrecipitation!!.parent as? ViewGroup
        parent?.removeView(dialogViewPrecipitation)

        val dialog = builder.create()
        dialog.show()

        radioGroupPrecipitation!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.preci_in -> {

                    preInBtn!!.isChecked = true
                    preMmBtn!!.isChecked = false

                    viewModel.precipitationValue("pre_in")
                    sharedPref.edit().putString(Constants.PRECIPITATION_VALUE, "pre_in").apply()
                }

                R.id.preci_mm -> {

                    preInBtn!!.isChecked = false
                    preMmBtn!!.isChecked = true

                    viewModel.precipitationValue("pre_mm")
                    sharedPref.edit().putString(Constants.PRECIPITATION_VALUE, "pre_mm").apply()
                }



            }
            dialog.dismiss()

        }
    }

    private fun showDialogForPressure() {


        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pressure Unit")
        builder.setView(dialogViewPressure)


        val parent = dialogViewPressure!!.parent as? ViewGroup
        parent?.removeView(dialogViewPressure)

        val dialog = builder.create()
        dialog.show()

        radioGroupPressure!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.pressure_in -> {

                    pressureInBtn!!.isChecked = true
                    pressureMbBtn!!.isChecked = false

                    viewModel.pressureValue("pressure_in")
                    sharedPref.edit().putString(Constants.PRESSURE_VALUE, "pressure_in").apply()
                }

                R.id.pressure_mb -> {

                    pressureInBtn!!.isChecked = false
                    pressureMbBtn!!.isChecked = true

                    viewModel.pressureValue("pressure_mb")
                    sharedPref.edit().putString(Constants.PRESSURE_VALUE, "pressure_mb").apply()
                }



            }
            dialog.dismiss()

        }
    }

    private fun showDialogForVisibility() {


        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Visibility Unit")
        builder.setView(dialogViewVisibility)


        val parent = dialogViewVisibility!!.parent as? ViewGroup
        parent?.removeView(dialogViewVisibility)

        val dialog = builder.create()
        dialog.show()

        radioGroupVisibility!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.vis_kms -> {

                    visKmBtn!!.isChecked = true
                    visMilesBtn!!.isChecked = false

                    viewModel.visibilityValue("vis_km")
                    sharedPref.edit().putString(Constants.VISIBILITY_VALUE, "vis_km").apply()
                }

                R.id.vis_miles -> {

                    visKmBtn!!.isChecked = false
                    visMilesBtn!!.isChecked = true

                    viewModel.visibilityValue("vis_miles")
                    sharedPref.edit().putString(Constants.VISIBILITY_VALUE, "vis_miles").apply()
                }



            }
            dialog.dismiss()

        }
    }
}













