package com.example.myweatherapplication.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapplication.*
import com.example.myweatherapplication.adapters.PrecipitationAdapter
import com.example.myweatherapplication.adapters.RVTomorrowAdapter
import com.example.myweatherapplication.adapters.WindAdapter
import com.example.myweatherapplication.data.weather.WeatherResponse
import com.example.myweatherapplication.databinding.FragmentTomorrowBinding
import com.example.myweatherapplication.models.Hour
import com.example.myweatherapplication.util.ScreenState
import com.example.myweatherapplication.viewmodels.WeatherViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TomorrowFragment : Fragment() {

    private lateinit var mBinding: FragmentTomorrowBinding

    // private  lateinit var viewModel: WeatherViewModel
    // private val viewModel: WeatherViewModel by viewModels()
    private lateinit var rvTomorrowAdapter: RVTomorrowAdapter
    private lateinit var rvWindAdapter: WindAdapter
    private lateinit var precipitationAdapter: PrecipitationAdapter
    private val viewModel: WeatherViewModel by viewModels({ requireParentFragment() })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentTomorrowBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //viewModel = (activity as MainActivity).viewModel


        viewModel.result.observe(viewLifecycleOwner) {
            it?.let { response ->
                when (response) {
                    is ScreenState.Success -> {

                        drawChartTest(response.data!!)
                        setImage(response.data.current.condition.code, response.data.current.is_day)
                        updateUI(response.data)
                        drawBarChartTest(response.data)

                    }

                    is ScreenState.Loading -> {

                    }

                    is ScreenState.Error -> {

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

                mBinding.scrolllTmrw.setBackgroundResource(R.drawable.gradient_bng_cloudy_partly)
                playAnimation(R.raw.partly_cloudy)
            }

            in 1008..1010->{
                mBinding.scrolllTmrw.setBackgroundResource(R.drawable.gradient_bng_cloud)
                playAnimation(R.raw.full_cloudy)
            }
            in 1051..1270->{
                mBinding.scrolllTmrw.setBackgroundResource(R.drawable.gradient_bng_rainy)
                playAnimation(R.raw.cloudyrain)
            }

            1000->{

                if(isDay == 1){
                    mBinding.scrolllTmrw.setBackgroundResource(R.drawable.gradient_bng_sunny)
                    playAnimation(R.raw.ssunny)
                }else {
                    mBinding.scrolllTmrw.setBackgroundResource(R.drawable.gradient_bng_cloudy_partly)
                    playAnimation(R.raw.moon)
                }
            }else->{
            mBinding.scrolllTmrw.setBackgroundResource(R.drawable.gradient_bng_cloudy_partly)
        }
        }
    }

    private fun setUpRecyclerView() {

        rvTomorrowAdapter = RVTomorrowAdapter()

        mBinding.rvForecastTmrw.apply {
            adapter = rvTomorrowAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)

        }

     // mBinding.rvForecastTmrw.scrollToPosition()


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

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun updateUI(it: WeatherResponse){


        mBinding.cityName.text = it.location.name
       val dateString = it.forecast.forecastday[1].date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date!!

        // Get the day of the week from the Calendar instance
       // val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Print the day of the week
        val dayOfWeekString = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)

        mBinding.tvDate.text = it.forecast.forecastday[1].date+ " " + "," + " " + dayOfWeekString

        viewModel.isCelsius.observe(viewLifecycleOwner) { value ->
            if (value) {
                mBinding.tvDay.text =
                    "Day" + " " + it.forecast.forecastday[1].day.maxtemp_c.toString() + "°" + " " + "↑,"
                mBinding.tvNight.text =
                    "Night" + " " + it.forecast.forecastday[1].day.mintemp_c.toString() + "°" + " " + "↓"
            } else {
                mBinding.tvDay.text =
                    "Day" + " " + it.forecast.forecastday[1].day.maxtemp_f.toString() + "°" + " " + "↑,"
                mBinding.tvNight.text =
                    "Night" + " " + it.forecast.forecastday[1].day.mintemp_f.toString() + "°" + " " + "↓"
            }
        }

        mBinding.tvWeather.text = it.forecast.forecastday[1].day.condition.text
      viewModel.windValue.observe(viewLifecycleOwner) { value ->
          when (value) {
              "km" -> {
                  mBinding.tvWindValue.text = it.current.wind_kph.toString()
                  mBinding.tvWindValueInNumsChart.text =
                      it.forecast.forecastday[0].hour[0].wind_kph.toString() + "Km/hr"
              }

              "mph" -> {
                  mBinding.tvWindValue.text = it.current.wind_mph.toString()
                  mBinding.tvWindValueInNumsChart.text =
                      it.forecast.forecastday[0].hour[0].wind_mph.toString() + "mph"
              }

              "degree" -> {
                  mBinding.tvWindValue.text = it.current.wind_degree.toString()
                  mBinding.tvWindValueInNumsChart.text =
                      it.forecast.forecastday[0].hour[0].wind_degree.toString() + "deg"
              }
          }
      }
        viewModel.visValue.observe(viewLifecycleOwner) { visValue ->
            when (visValue) {
                "vis_km" -> {
                    mBinding.tvVisibilityValue.text = it.current.vis_km.toString()
                }

                "vis_miles" -> {
                    mBinding.tvVisibilityValue.text = it.current.vis_miles.toString()
                }
            }
        }
        viewModel.pressureValue.observe(viewLifecycleOwner) { preValue ->
            when (preValue) {
                "pressure_in" -> {
                    mBinding.tvPressureValue.text = it.current.pressure_in.toString()
                }

                "pressure_mb" -> {
                    mBinding.tvPressureValue.text = it.current.pressure_mb.toString()
                }
            }
        }
        viewModel.preciValue.observe(viewLifecycleOwner) { preciValue ->
            when (preciValue) {
                "pre_in" -> {
                    mBinding.tvPrecipitationSizeValue.text = it.current.precip_in.toString() + "in"
                }

                "pre_mm" -> {
                    mBinding.tvPrecipitationSizeValue.text = it.current.precip_in.toString() + "mm"
                }
            }
        }
        mBinding.tvSunriseValue.text = it.forecast.forecastday[1].astro.sunrise
        mBinding.tvSunsetValue.text = it.forecast.forecastday[1].astro.sunset
        mBinding.tvUvIndexValue.text = it.current.uv.toString()
        mBinding.tvChanceOfRainValue.text =
            it.forecast.forecastday[0].day.daily_chance_of_rain.toString()
        mBinding.tvWindValueChart.text = it.current.wind_dir

    }



    class DegreeValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "$value°"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun drawChartTest(it: WeatherResponse) {
        val lineChart = mBinding.lineChart
      //  rvTomorrowAdapter.differ.submitList(it.forecast.forecastday[1].hour)
        val list = ArrayList<Hour>()
        val listToday = it.forecast.forecastday[1]
        val listTomorrow = it.forecast.forecastday[2]
        val listTime = ArrayList<String>()
        val listTempEntries = ArrayList<Float>()
        val entries = ArrayList<Entry>()
        val arrayListX = ArrayList<String>()

                val input = SimpleDateFormat("yyyy-MM-dd hh:mm")
                val output = SimpleDateFormat("hh:mm aa")
        val hoursToday = listToday.hour

        viewModel.isCelsius.observe(viewLifecycleOwner) { temp ->

            list.clear()
            listTime.clear()
            listTempEntries.clear()
            entries.clear()
            arrayListX.clear()

            for (item in hoursToday.indices) {
                val hour = hoursToday[item]
                val date = input.parse(hour.time)
                val t = output.format(date!!)
             //   val currentTime = it.location.localtime
              //  val crntDate = input.parse(currentTime)
              //  val time = output.format(crntDate!!)

                val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                val tinput = outputFormat.parse(t)
                val toutput = inputFormat.format(tinput!!)

                //  val timeInput = outputFormat.parse(time)
                //   val timeOutput  = inputFormat.format(timeInput!!)
                val timeOutput = "06:00 AM"

                //  val timeFormat = DateTimeFormatter.ofPattern("hh:mm a")


                val compareResult = toutput.compareTo(timeOutput)

                if (compareResult >= 0) {
                    val newTime = outputFormat.parse(t)
                    val newTime1 = inputFormat.format(newTime!!)
                    listTime.add(newTime1)
                    val tempValue = if (temp) hour.temp_c.toFloat() else hour.temp_f.toFloat()
                    //  listTempEntries.add(hour.temp_c.toFloat())
                    listTempEntries.add(tempValue)
                    list.add(hour)

                }
            }
            for (i in listTomorrow.hour) {
                val date2 = input.parse(i.time)
                val t2 = output.format(date2!!)
                if (listTime.size <= 24) {
                    listTime.add(t2)
                    if (listTempEntries.size <= 24) {
                        val temValue = if (temp) i.temp_c.toFloat() else i.temp_f.toFloat()
                        // listTempEntries.add(i.temp_c.toFloat())
                        listTempEntries.add(temValue)
                    }

                }
                if (list.size <= 24) {
                    list.add(i)
                }
            }
            rvTomorrowAdapter.differ.submitList(list)


            for (a in listTempEntries.indices) {
                val s = listTempEntries[a]
                entries.add(Entry(a.toFloat(), s))
            }

            for (x in listTime.indices) {
                val y = listTime[x]
                if (arrayListX.size <= 24) {
                    arrayListX.add(y)
                }

                val xAxis = lineChart.xAxis
                // xAxis.labelCount = 3
                xAxis.valueFormatter = IndexAxisValueFormatter(arrayListX)
                // xAxis.granularity = 1f
                //  xAxis.isGranularityEnabled = true

                val dataSet = LineDataSet(entries, "Temperature")
                dataSet.valueFormatter = DegreeValueFormatter()

                dataSet.lineWidth = 2f
                dataSet.color = Color.parseColor("#2697f4")
                dataSet.valueTextSize = 13f
                dataSet.valueTextColor = Color.WHITE
                dataSet.setDrawCircles(false)
                //  dataSet.setDrawCircleHole(true)
                //  dataSet.circleRadius = 7f
                //   dataSet.circleHoleRadius = 4f
                //   dataSet.setCircleColor(Color.BLUE)
                //   dataSet.circleHoleColor = Color.parseColor("#f426df")
                dataSet.setDrawFilled(true)
                dataSet.fillColor = Color.parseColor("#2697f4")
                dataSet.setDrawValues(true)
                //  dataSet.fillDrawable = ContextCompat.getDrawable(requireContext(),R.drawable.background_round)
                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

                val line_data = LineData(dataSet)

                lineChart.data = line_data

                lineChart.setVisibleXRangeMaximum(24f)
                //  xAxis.axisMinimum = floatTime
                lineChart.moveViewToX(0f)

                //lineChart.moveViewToX(1f)
                //  lineChart.scrollX
                lineChart.invalidate()
                // lineChart.notifyDataSetChanged()
                // lineChart.animateXY(3000,3000)
                xAxis.setDrawGridLines(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM


                lineChart.setDrawGridBackground(false) // Disable grid background
                lineChart.setDrawBorders(false) // Disable chart borders
                lineChart.description.isEnabled = false // Disable chart description
                lineChart.legend.isEnabled = false // Disable legend

// Disable x-axis
                lineChart.xAxis.isEnabled = false

// Disable left and right y-axes
                lineChart.axisLeft.isEnabled = false
                lineChart.axisRight.isEnabled = false

// Disable extra offsets (padding)
                lineChart.extraLeftOffset = 0f
                lineChart.extraRightOffset = 0f
                lineChart.extraTopOffset = 0f
                lineChart.extraBottomOffset = 0f

// Disable interaction gestures
                lineChart.isScaleXEnabled = false
                lineChart.isScaleYEnabled = false
                lineChart.setPinchZoom(false)
                lineChart.isDoubleTapToZoomEnabled = false
                lineChart.isHighlightPerDragEnabled = false
                lineChart.isHighlightPerTapEnabled = false

// Disable data labels
                lineChart.setDrawMarkers(false)

            }


        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun drawBarChartTest(it: WeatherResponse) {
        val barChart = mBinding.barChart
        //  rvTomorrowAdapter.differ.submitList(it.forecast.forecastday[1].hour)
        val list = ArrayList<Hour>()
        val listToday = it.forecast.forecastday[1]
        val listTomorrow = it.forecast.forecastday[2]
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
                    //  val currentTime = it.location.localtime
               // val crntDate = input.parse(currentTime)
               // val time = output.format(crntDate!!)

                val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                val tinput = outputFormat.parse(t)
                val toutput = inputFormat.format(tinput!!)

                //   val timeInput = outputFormat.parse(time)
                //  val timeOutput  = inputFormat.format(timeInput!!)

                val timeOutput = "06:00 AM"
                //  val timeFormat = DateTimeFormatter.ofPattern("hh:mm a")


                val compareResult = toutput.compareTo(timeOutput)

                if (compareResult >= 0) {
                    val newTime = outputFormat.parse(t)
                    val newTime1 = inputFormat.format(newTime!!)
                    listTime.add(newTime1)
                    val windValue =
                        if (windUnit == "km") hour.wind_kph.toFloat() else hour.wind_mph.toFloat()
                    listTempEntries.add(windValue)
                    list.add(hour)

                }
            }
            for (i in listTomorrow.hour) {
                val date2 = input.parse(i.time)
                val t2 = output.format(date2!!)
                if (listTime.size <= 24) {
                    listTime.add(t2)
                    if (listTempEntries.size <= 24) {
                        val windValue =
                            if (windUnit == "km") i.wind_kph.toFloat() else i.wind_mph.toFloat()
                        listTempEntries.add(windValue)
                    }

                }
                if (list.size <= 24) {
                    list.add(i)
                }
            }
            rvWindAdapter.differ.submitList(list)
            precipitationAdapter.differ.submitList(list)

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






}