package com.example.myweatherapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapplication.databinding.ItemForecastBinding
import com.example.myweatherapplication.models.Forecastday
import com.example.myweatherapplication.viewmodels.WeatherViewModel
import com.squareup.picasso.Picasso

class WeatherAdapter(val context: Context,val viewModel: WeatherViewModel,val lifecycleOwner: LifecycleOwner):RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    private var binding: ItemForecastBinding? = null

    inner class WeatherViewHolder(itemBinding: ItemForecastBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object :
        DiffUtil.ItemCallback<Forecastday>() {
        override fun areItemsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Forecastday, newItem: Forecastday): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        binding = ItemForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherViewHolder(binding!!)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {

        val forecast = differ.currentList[position]

        holder.itemView.apply {

            viewModel.isCelsius.observe(lifecycleOwner) {
                if (it) {
                    binding?.maxTemp?.text = forecast.day.maxtemp_c.toString()
                    binding?.minTemp?.text = forecast.day.mintemp_c.toString()
                } else {
                    binding?.maxTemp?.text = forecast.day.maxtemp_f.toString()
                    binding?.minTemp?.text = forecast.day.mintemp_f.toString()
                }
            }


            // Glide.with(this).load(forecast.day.condition.icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding?.weatherImage!!)
            binding?.tvWeather?.text = forecast.day.condition.text
          //  binding?.weatherImage?.setImageResource(forecast.day.condition.icon.toInt())
            Picasso.get().load(forecast.day.condition.icon).into(binding?.weatherImage)
            val isExpandable:Boolean = forecast.isExpandable
            binding?.expandableLayout?.visibility = if(isExpandable) View.VISIBLE else View.GONE
            binding?.view?.visibility = if(isExpandable) View.INVISIBLE else View.VISIBLE

            binding?.rlForecast?.setOnClickListener {
                forecast.isExpandable = !forecast.isExpandable
                notifyItemChanged(position)
            }
            binding?.windValue?.text = forecast.day.maxwind_kph.toString()+"Km/hr"
            binding?.humidityValue?.text = forecast.day.avghumidity.toString()
            binding?.rainValue?.text = forecast.day.daily_chance_of_rain.toString()+"%"
            binding?.sunValue?.text = forecast.astro.sunrise

            val adapterRV = RVTomorrowAdapter()
            binding?.rvHour?.adapter = adapterRV
            binding?.rvHour?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            binding?.rvHour?.setHasFixedSize(true)
            adapterRV.differ.submitList(forecast.hour)
        }

  holder.setIsRecyclable(false)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}