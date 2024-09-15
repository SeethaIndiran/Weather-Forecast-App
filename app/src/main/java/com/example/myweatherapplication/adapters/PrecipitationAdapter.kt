package com.example.myweatherapplication.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapplication.R
import com.example.myweatherapplication.databinding.RvItemPrecipitationBinding

import com.example.myweatherapplication.models.Hour
import com.example.myweatherapplication.viewmodels.WeatherViewModel
import java.text.ParseException
import java.text.SimpleDateFormat

class PrecipitationAdapter(private val viewModel: WeatherViewModel,private val lifecycleOwner: LifecycleOwner):RecyclerView.Adapter<PrecipitationAdapter.WeatherViewHolder>() {

    private var binding: RvItemPrecipitationBinding? = null

    inner class WeatherViewHolder(itemBinding: RvItemPrecipitationBinding) :
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
        binding = RvItemPrecipitationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeatherViewHolder(binding!!)

    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {

        val hour = differ.currentList[position]

        holder.itemView.apply {

          //  binding?.tvTemp?.text = hour.precip_mm.toString()
           // Glide.with(this).load(hour.condition.icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding?.ivImage!!)
           // binding?.ivImage?.setImageResource(hour.condition.icon.toInt())
    //  Picasso.get().load(hour.condition.icon).into(binding?.ivImage!!)
          //  binding?.tvTemp?.setTextColor(Color.parseColor("#FFFFFF"))
            viewModel.preciValue.observe(lifecycleOwner) {
                if (it == "pre_in") {
                    binding?.tvPreciVolume?.text = hour.precip_in.toString()
                } else {
                    binding?.tvPreciVolume?.text = hour.precip_mm.toString()
                }
            }
            binding?.tvChance?.text = hour.chance_of_rain.toString()+"%"

            val preci = hour.chance_of_rain
            if(preci > 0){
               binding?.ivPrecipitation?.setImageResource(R.drawable.water_drop)
            }else{
                binding?.ivPrecipitation?.setImageResource(R.drawable.water_drop_black)
            }

            val input = SimpleDateFormat("yyyy-MM-dd hh:mm")
            val output = SimpleDateFormat("hh:mm aa")
            try{
                val date = input.parse(hour.time)
                val t = output.format(date!!)
                binding?.tvPreciTime?.text =t
            }catch(e: ParseException){
                e.printStackTrace()

            }        }

        holder.setIsRecyclable(false)


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}