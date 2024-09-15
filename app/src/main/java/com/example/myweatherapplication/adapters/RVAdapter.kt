package com.example.myweatherapplication.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapplication.databinding.RvItemBinding
import com.example.myweatherapplication.models.Hour
import com.example.myweatherapplication.viewmodels.WeatherViewModel
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat

 class RVAdapter(private val viewModel: WeatherViewModel,private val lifecycleOwner: LifecycleOwner):RecyclerView.Adapter<RVAdapter.WeatherViewHolder>() {

    private var binding: RvItemBinding? = null

    inner class WeatherViewHolder(itemBinding: RvItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object :
        DiffUtil.ItemCallback<Hour>() {
        override fun areItemsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        binding = RvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return WeatherViewHolder(binding!!)

    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {

        val hour = differ.currentList[position]

        holder.itemView.apply {

            viewModel.isCelsius.observe(lifecycleOwner) {
                if (it) {
                    binding?.tvTemp?.text = hour.temp_c.toString() + " °C"
                } else {
                    binding?.tvTemp?.text = hour.temp_f.toString() + "°F"
                }
            }

            viewModel.windValue.observe(lifecycleOwner) {
                when (it) {
                    "km" -> {
                        binding?.windSpeed?.text = hour.wind_kph.toString() + "Km/hr"
                    }

                    "mph" -> {
                        binding?.windSpeed?.text = hour.wind_mph.toString() + "mph"
                    }

                    "degree" -> {
                        binding?.windSpeed?.text = hour.wind_degree.toString() + "deg"
                    }

                }
            }


            // Glide.with(this).load(hour.condition.icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding?.ivImage!!)

           // binding?.ivImage?.setImageResource(hour.condition.icon.toInt())
            Picasso.get().load(hour.condition.icon).into(binding?.ivImage!!)


            val input = SimpleDateFormat("yyyy-MM-dd hh:mm")
            val output = SimpleDateFormat("hh:mm aa")
            try{
                      val date = input.parse(hour.time)
                      val t = output.format(date!!)
                binding?.tvTime?.text = t
        }catch(e:ParseException){
            e.printStackTrace()

            }        }

        holder.setIsRecyclable(false)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }




}