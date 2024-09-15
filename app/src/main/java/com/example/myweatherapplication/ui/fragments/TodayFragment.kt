package com.example.myweatherapplication.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapplication.*
import com.example.myweatherapplication.adapters.PrecipitationAdapter
import com.example.myweatherapplication.adapters.RVAdapter
import com.example.myweatherapplication.adapters.WindAdapter
import com.example.myweatherapplication.data.weather.WeatherResponse
import com.example.myweatherapplication.databinding.FragmentTodayBinding
import com.example.myweatherapplication.models.Hour
import com.example.myweatherapplication.util.ScreenState
import com.example.myweatherapplication.viewmodels.WeatherViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TodayFragment : Fragment(){

    private  lateinit var mBinding: FragmentTodayBinding
    //private  lateinit var viewModel:WeatherViewModel
      lateinit var rvAdapter: RVAdapter
    private var mProgressDialog: Dialog? = null
    private lateinit var rvWindAdapter: WindAdapter
    private lateinit var precipitationAdapter: PrecipitationAdapter

    @Inject
    lateinit var sharedPref: SharedPreferences

     private val viewModel : WeatherViewModel by viewModels({requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentTodayBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



      //  setUpRecyclerViewPrecipitation()

        viewModel.result.observe(viewLifecycleOwner) {
            it?.let { response ->
                when (response) {
                    is ScreenState.Success -> {
                        hideCustomProgressDialog()
                        updateRecyclerView(response.data!!)
                        drawBarChartTest(response.data)
                        setUpUI(response.data)
                        setImage(response.data.current.condition.code, response.data.current.is_day)
                        // rvAdapter.notifyDataSetChanged()

                    }

                    is ScreenState.Loading -> {
                        showCustomProgressDialog()
                    }

                    is ScreenState.Error -> {
                        hideCustomProgressDialog()
                    }
                }
            }
        }
        setUpRecyclerView()
       setUpRecyclerViewForWind()
        setUpRecyclerViewPrecitation()

    }

    private fun playAnimation(res:Int){
        mBinding.ivImage.setAnimation(res)
        mBinding.ivImage.playAnimation()
    }



    private fun setImage(code:Int,isDay:Int){

        when(code){
            in 1001..1007->{

                mBinding.scrollView.setBackgroundResource(R.drawable.gradient_bng_cloudy_partly)
                playAnimation(R.raw.partly_cloudy)
            }

            in 1008..1010->{
                mBinding.scrollView.setBackgroundResource(R.drawable.gradient_bng_cloud)
                playAnimation(R.raw.full_cloudy)
            }
            in 1051..1270->{
                mBinding.scrollView.setBackgroundResource(R.drawable.gradient_bng_rainy)
                playAnimation(R.raw.cloudyrain)
            }

            1000->{

                if(isDay == 1){
                    mBinding.scrollView.setBackgroundResource(R.drawable.gradient_bng_sunny)
                    playAnimation(R.raw.ssunny)
                }else {
                    mBinding.scrollView.setBackgroundResource(R.drawable.gradient_bng_cloudy_partly)
                    playAnimation(R.raw.moon)
                }
            }else->{
            mBinding.scrollView.setBackgroundResource(R.drawable.gradient_bng_cloudy_partly)
        }
        }
    }

    @SuppressLint("SetTextI18n", "Range", "SimpleDateFormat")
     fun setUpUI(it: WeatherResponse){
 //val tempUnit = sharedPref.getBoolean(Constants.TEMP_UNIT,true)

        hideCustomProgressDialog()



//                                       preAdapter.differ.submitList(it.forecast.forecastday[0].hour)

                                       // windAdapter.differ.submitList(it.forecast.forecastday[0].hour)



                                     viewModel.isCelsius.observe(viewLifecycleOwner) { tempUnit ->


                                         if (tempUnit) {
                                             mBinding.tvTemp.text =
                                                 it.current.temp_c.toString() + " " + "°C"
                                             mBinding.tvDay.text =
                                                 "Day" + " " + it.forecast.forecastday[0].day.maxtemp_c.toString() + "°" + " " + "↑,"
                                             mBinding.tvNight.text =
                                                 "Night" + " " + it.forecast.forecastday[0].day.mintemp_c.toString() + "°" + " " + "↓"

                                         } else {
                                             mBinding.tvTemp.text =
                                                 it.current.temp_f.toString() + "" + "°F"
                                             mBinding.tvDay.text =
                                                 "Day" + " " + it.forecast.forecastday[0].day.maxtemp_f.toString() + "°" + " " + "↑,"
                                             mBinding.tvNight.text =
                                                 "Night" + " " + it.forecast.forecastday[0].day.mintemp_f.toString() + "°" + " " + "↓"
                                         }

                                     }


        //  Glide.with(this).load(it.current.condition.icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(mBinding.ivImage)



                                     // Picasso.get().load(it.current.condition.icon).into(mBinding.ivImage)
        mBinding.ivImage.setImageResource(R.drawable.sunny)
                                       mBinding.tvFeelsLike.text =
                                           "Feels like" + " " + it.current.feelslike_c.toString() + "°"
                                       mBinding.tvWeather.text = it.current.condition.text
                                       mBinding.cityName.text = it.location.name
                                       mBinding.tvSunriseValue.text =
                                           it.forecast.forecastday[0].astro.sunrise
                                       mBinding.tvSunsetValue.text =
                                           it.forecast.forecastday[0].astro.sunset

        viewModel.windValue.observe(viewLifecycleOwner) { windValue ->
            if (windValue == "km") {
                mBinding.tvWindValue.text =
                    it.forecast.forecastday[0].hour[0].wind_kph.toString() + "Km/hr"
                mBinding.tvWindValueInNumsChart.text =
                    it.forecast.forecastday[0].hour[0].wind_kph.toString() + "Km/hr"
            } else if (windValue == "mph") {
                mBinding.tvWindValue.text =
                    it.forecast.forecastday[0].hour[0].wind_mph.toString() + "mph"
                mBinding.tvWindValueInNumsChart.text =
                    it.forecast.forecastday[0].hour[0].wind_mph.toString() + "mph"
            } else if (windValue == "degree") {
                mBinding.tvWindValue.text =
                    it.forecast.forecastday[0].hour[0].wind_degree.toString() + "degree"
                mBinding.tvWindValueInNumsChart.text =
                    it.forecast.forecastday[0].hour[0].wind_degree.toString() + "degree"
            }

        }

        mBinding.tvUvIndexValue.text = it.current.uv.toString()

        viewModel.pressureValue.observe(viewLifecycleOwner) { value ->
            when (value) {
                "pressure_in" -> {
                    mBinding.tvPressureValue.text = it.current.pressure_in.toString() + "in"
                }

                "pressure_mb" -> {
                    mBinding.tvPressureValue.text = it.current.pressure_in.toString() + "mBar"
                }
            }
        }
        mBinding.tvWindValueChart.text = it.current.wind_dir
        viewModel.preciValue.observe(viewLifecycleOwner) { value ->
            when (value) {
                "pre_in" -> {
                    mBinding.tvPrecipitationSizeValue.text = it.current.precip_in.toString() + "in"
                }

                "pre_mm" -> {
                    mBinding.tvPrecipitationSizeValue.text = it.current.precip_mm.toString() + "mm"
                }
            }
        }

        mBinding.tvChanceOfRainValue.text =
                                           it.forecast.forecastday[0].day.daily_chance_of_rain.toString()

        viewModel.visValue.observe(viewLifecycleOwner) { vis_value ->
            when (vis_value) {
                "vis_km" -> {
                    mBinding.tvVisibilityValue.text = it.current.vis_km.toString() + "Km"
                }

                "vis_miles" -> {
                    mBinding.tvVisibilityValue.text = it.current.vis_km.toString() + "miles"
                }
            }

        }

        //  mBinding.tvWindNumber.text =
                                     //      it.current.wind_kph.toString() + "Km/h"
                                    //   mBinding.tvPrecipitationNumber.text =
                                     //      it.current.precip_mm.toString() + "mm"

                                       val sdf = SimpleDateFormat("EEEE")
                                       val d = Date()
                                       val day = sdf.format(d)

                                       mBinding.tvDayTime.text =
                                           it.forecast.forecastday[0].date + " " + "," + " " + day

        if (it.current.is_day == 0) {
            mBinding.scrollView.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.stars
                )
          //  mBinding.clThree.setBackgroundColor(Color.parseColor("#01579b"))
          //  mBinding.clFour.setBackgroundColor(Color.parseColor("#01579b"))
           // mBinding.clFive.setBackgroundColor(Color.parseColor("#01579b"))
        } else {
            mBinding.scrollView.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.clear_sky
                )
           // mBinding.clThree.setBackgroundColor(Color.parseColor("#0288d1"))
          //  mBinding.clFour.setBackgroundColor(Color.parseColor("#0288d1"))
         //   mBinding.clFive.setBackgroundColor(Color.parseColor("#0288d1"))
        }



                                   }





    private  fun showCustomProgressDialog(){
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideCustomProgressDialog(){
        mProgressDialog?.dismiss()
    }


    private  fun setUpRecyclerView(){

        rvAdapter  = RVAdapter(viewModel,viewLifecycleOwner)

        mBinding.rvForecast.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
            setHasFixedSize(true)

        }




    }

    private fun setUpRecyclerViewForWind() {

        rvWindAdapter = WindAdapter()

        mBinding.rvWind.apply {
            adapter = rvWindAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)

        }

        // mBinding.rvForecastTmrw.scrollToPosition()


    }

    private fun setUpRecyclerViewPrecitation() {

        precipitationAdapter = PrecipitationAdapter(viewModel,viewLifecycleOwner)

        mBinding.rvPrecipitation.apply {
            adapter = precipitationAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)

        }

        // mBinding.rvForecastTmrw.scrollToPosition()


    }
    @SuppressLint("SimpleDateFormat")
    private fun updateRecyclerView(it: WeatherResponse){
        val list = ArrayList<Hour>()
        val listToday = it.forecast.forecastday[0]
        val listTomorrow = it.forecast.forecastday[1]
        val listTime = ArrayList<String>()
        val listTempEntries = ArrayList<Float>()

        val input = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val output = SimpleDateFormat("hh:mm aa")
        val hoursToday = listToday.hour

       // viewModel.windValue.observe(viewLifecycleOwner, Observer {windUnit->
            list.clear()
            listTime.clear()
            listTempEntries.clear()



        for(item in hoursToday.indices) {
            val hour = hoursToday[item]
            val date = input.parse(hour.time)
            val t = output.format(date!!)
            val currentTime = it.location.localtime
            val crntDate = input.parse(currentTime)
            val time = output.format(crntDate!!)

            val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())

            val tinput = outputFormat.parse(t)
            val toutput  = inputFormat.format(tinput!!)

            val timeInput = outputFormat.parse(time)
            val timeOutput  = inputFormat.format(timeInput!!)

            //  val timeFormat = DateTimeFormatter.ofPattern("hh:mm a")


            val compareResult = toutput.compareTo(timeOutput)

            if (compareResult >=0) {
                val newTime = outputFormat.parse(t)
                val newTime1 = inputFormat.format(newTime!!)
                listTime.add(newTime1)
 //  val windValue = if(windUnit == "km") hour.wind_kph.toFloat() else hour.wind_mph.toFloat()
                listTempEntries.add(hour.wind_kph.toFloat())
                list.add(hour)

            }
        }
        for(i in listTomorrow.hour) {
            val date2 = input.parse(i.time)
            val t2 = output.format(date2!!)
            if (listTime.size <= 24) {
                listTime.add(t2)
                if (listTempEntries.size <= 24) {
//val windValue = if(windUnit == "km") i.wind_kph.toFloat() else i.wind_mph.toFloat()

                     listTempEntries.add(i.wind_kph.toFloat())
                }

            }
            if(list.size<=24){
                list.add(i)
            }
        }

        precipitationAdapter.differ.submitList(list)
        precipitationAdapter.notifyDataSetChanged()
        rvAdapter.differ.submitList(list)
        rvAdapter.notifyDataSetChanged()
       // })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun drawBarChartTest(it: WeatherResponse) {
        val barChart = mBinding.barChart
        //  rvTomorrowAdapter.differ.submitList(it.forecast.forecastday[1].hour)
        val list = ArrayList<Hour>()
        val listToday = it.forecast.forecastday[0]
        val listTomorrow = it.forecast.forecastday[1]
        val listTime = ArrayList<String>()
        val listTempEntries = ArrayList<Float>()
        val entries = ArrayList<BarEntry>()
        val arrayListX = ArrayList<String>()

        val input = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val output = SimpleDateFormat("hh:mm aa")
        val hoursToday = listToday.hour

        viewModel.windValue.observe(viewLifecycleOwner) { windUnit ->
            list.clear()
            listTime.clear()
            listTempEntries.clear()
            entries.clear()
            arrayListX.clear()


            for (item in hoursToday.indices) {
                val hour = hoursToday[item]
                val date = input.parse(hour.time)
                val t = output.format(date!!)
                val currentTime = it.location.localtime
                val crntDate = input.parse(currentTime)
                val time = output.format(crntDate!!)
              //  var windValue = 0F
                val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                val tinput = outputFormat.parse(t)
                val toutput = inputFormat.format(tinput!!)

                val timeInput = outputFormat.parse(time)
                val timeOutput = inputFormat.format(timeInput!!)

                //  val timeFormat = DateTimeFormatter.ofPattern("hh:mm a")


                val compareResult = toutput.compareTo(timeOutput)

                if (compareResult >= 0) {
                    val newTime = outputFormat.parse(t)
                    val newTime1 = inputFormat.format(newTime!!)
                    listTime.add(newTime1)
                    val wind_value =
                        if (windUnit == "km") hour.wind_kph.toFloat() else hour.wind_mph.toFloat()
                    // listTempEntries.add(hour.wind_kph.toFloat())
                    listTempEntries.add(wind_value)

                    list.add(hour)

                }
            }
            for (i in listTomorrow.hour) {
                val date2 = input.parse(i.time)
                val t2 = output.format(date2!!)


                if (listTime.size <= 24) {
                    listTime.add(t2)
                    if (listTempEntries.size <= 24) {
                        //   listTempEntries.add(i.wind_kph.toFloat())
                        val wind =
                            if (windUnit == "km") i.wind_kph.toFloat() else i.wind_mph.toFloat()
                        listTempEntries.add(wind)

                    }

                }
                if (list.size <= 24) {
                    list.add(i)
                }
                rvWindAdapter.differ.submitList(list)

            }


            for (a in listTempEntries.indices) {
                val s = listTempEntries[a]
                entries.add(BarEntry(a.toFloat(), s))
            }

            for (x in listTime.indices) {
                val y = listTime[x]
                if (arrayListX.size <= 24) {
                    arrayListX.add(y)
                }

                val xAxis = barChart.xAxis
                // xAxis.labelCount = 3
                xAxis.valueFormatter = IndexAxisValueFormatter(arrayListX)
                // xAxis.granularity = 1f
                //  xAxis.isGranularityEnabled = true

                val dataSet = BarDataSet(entries, "Wind")
                //  dataSet.valueFormatter = HighlightedValueFormatter()
                //  dataSet.isHighlightEnabled = true


                // dataSet.lineWidth = 2f
                dataSet.color = Color.parseColor("#2697f4")
                dataSet.valueTextSize = 11f
                dataSet.valueTextColor = Color.BLACK
                // dataSet.valueFormatter = HighlightedValueFormatter()

                val bar_data = BarData(dataSet)
                bar_data.barWidth = 0.5f
                bar_data.setDrawValues(true)

                barChart.data = bar_data

                // Set a custom value formatter for the y-axis labels
                // dataSet.valueFormatter = DegreeValueFormatter()

                barChart.setVisibleXRangeMaximum(34f)
                //  xAxis.axisMinimum = floatTime
                barChart.moveViewToX(0f)
                //  barChart.setDrawValueAboveBar(true)
                // barChart.setFitBars(true)
                // barChart.groupBars(0f,0.1f,0.05f)

                //lineChart.moveViewToX(1f)
                //  lineChart.scrollX
                barChart.invalidate()
                // lineChart.notifyDataSetChanged()
                barChart.animateXY(3000, 3000)
                xAxis.setDrawGridLines(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM


                barChart.setDrawGridBackground(false) // Disable grid background
                barChart.setDrawBorders(false) // Disable chart borders
                barChart.description.isEnabled = false // Disable chart description
                barChart.legend.isEnabled = false // Disable legend

// Disable x-axis
                barChart.xAxis.isEnabled = false

// Disable left and right y-axes
                barChart.axisLeft.isEnabled = false
                barChart.axisRight.isEnabled = false

// Disable extra offsets (padding)
                barChart.extraLeftOffset = 0f
                barChart.extraRightOffset = 0f
                barChart.extraTopOffset = 0f
                barChart.extraBottomOffset = 0f

// Disable interaction gestures
                barChart.isScaleXEnabled = false
                barChart.isScaleYEnabled = false
                barChart.setPinchZoom(false)
                barChart.isDoubleTapToZoomEnabled = false
                barChart.isHighlightPerDragEnabled = false
                barChart.isHighlightPerTapEnabled = false

// Disable data labels
                barChart.setDrawMarkers(false)


            }
        }
    }

   /* private fun drawBarChartTest2(it: WeatherResponse) {
        val barChart = mBinding.barChart
        val listTime = ArrayList<String>()
        val listTempEntries = ArrayList<Float>()
        val entries = ArrayList<BarEntry>()
        val arrayListX = ArrayList<String>()

        val input = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val output = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val hoursToday = it.forecast.forecastday[0].hour
        val hoursTomorrow = it.forecast.forecastday[1].hour

        val currentTime = it.location.localtime
        val crntDate = input.parse(currentTime)
        val formattedCurrentTime = output.format(crntDate!!)

        val currentTimeInput = outputFormat.parse(formattedCurrentTime)
        val currentTimeOutput = inputFormat.format(currentTimeInput!!)

        viewModel.windValue.observe(viewLifecycleOwner, Observer { windUnit ->
            listTime.clear()
            listTempEntries.clear()
            entries.clear()
           arrayListX.clear()

            // Process today's hours
            for (hour in hoursToday) {
                val date = input.parse(hour.time)
                val formattedTime = output.format(date!!)

                val timeInput = outputFormat.parse(formattedTime)
                val timeOutput = inputFormat.format(timeInput!!)

                if (timeOutput.compareTo(currentTimeOutput) >= 0) {
                    val windValue = if (windUnit == "km") hour.wind_kph.toFloat() else hour.wind_mph.toFloat()
                    listTime.add(timeOutput)
                    listTempEntries.add(windValue)
                }
            }

            // Process tomorrow's hours
            for (hour in hoursTomorrow) {
                if (listTime.size <= 24) {
                    val date = input.parse(hour.time)
                    val formattedTime = output.format(date!!)
                    val windValue = if (windUnit == "km") hour.wind_kph.toFloat() else hour.wind_mph.toFloat()
                    listTime.add(formattedTime)
                    listTempEntries.add(windValue)
                }
            }

            for (i in listTempEntries.indices) {
                entries.add(BarEntry(i.toFloat(), listTempEntries[i]))
            }

            for (x in listTime.indices) {
                if (arrayListX.size <= 24) {
                    arrayListX.add(listTime[x])
                }
            }

            val xAxis = barChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(arrayListX)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.isEnabled = false

            val dataSet = BarDataSet(entries, "Wind")
            dataSet.color = Color.parseColor("#2697f4")
            dataSet.valueTextSize = 11f
            dataSet.valueTextColor = Color.BLACK

            val barData = BarData(dataSet)
            barData.barWidth = 0.5f

            barChart.data = barData
            barChart.setVisibleXRangeMaximum(34f)
            barChart.moveViewToX(0f)
            barChart.invalidate()
            barChart.animateXY(3000, 3000)
            barChart.setDrawGridBackground(false)
            barChart.setDrawBorders(false)
            barChart.description.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.axisLeft.isEnabled = false
            barChart.axisRight.isEnabled = false
            barChart.extraLeftOffset = 0f
            barChart.extraRightOffset = 0f
            barChart.extraTopOffset = 0f
            barChart.extraBottomOffset = 0f
            barChart.isScaleXEnabled = false
            barChart.isScaleYEnabled = false
            barChart.setPinchZoom(false)
            barChart.isDoubleTapToZoomEnabled = false
            barChart.isHighlightPerDragEnabled = false
            barChart.isHighlightPerTapEnabled = false
            barChart.setDrawMarkers(false)
        })
    }*/


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        viewModel.result.observe(viewLifecycleOwner) {
            it?.let { response ->
                when (response) {
                    is ScreenState.Success -> {

                        updateRecyclerView(response.data!!)
                        drawBarChartTest(response.data)
                        setUpUI(response.data)
                        setImage(response.data.current.condition.code, response.data.current.is_day)

                    }

                    is ScreenState.Loading -> {

                    }

                    is ScreenState.Error -> {

                    }
                }
            }
        }

    }

}


