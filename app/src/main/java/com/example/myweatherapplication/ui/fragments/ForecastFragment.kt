package com.example.myweatherapplication.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapplication.adapters.WeatherAdapter
import com.example.myweatherapplication.databinding.FragmentForecastBinding
import com.example.myweatherapplication.util.ScreenState
import com.example.myweatherapplication.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForecastFragment : Fragment() {

    private  lateinit var mBinding: FragmentForecastBinding
   // private  lateinit var viewModel: WeatherViewModel


    private  lateinit var weatherAdapter : WeatherAdapter
    //private  val number:Int = 5
    private val viewModel : WeatherViewModel by viewModels({requireParentFragment()})



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentForecastBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel = (activity as MainActivity).viewModel


       setUpRecyclerView()

        viewModel.result.observe(viewLifecycleOwner) {
            it?.let { response ->
                when (response) {
                    is ScreenState.Success -> {
                        weatherAdapter.differ.submitList(response.data!!.forecast.forecastday)

                    }

                    is ScreenState.Loading -> {

                    }

                    is ScreenState.Error -> {

                    }
                }
            }
        }
        // setUpUI()


    }






    private  fun setUpRecyclerView(){

        weatherAdapter = WeatherAdapter(requireContext(),viewModel,viewLifecycleOwner)
        mBinding.rvForecast.apply{
            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
            adapter = weatherAdapter
          //  addItemDecoration(object :DividerItemDecoration(activity,LinearLayout.VERTICAL){})



        }




    }




}


