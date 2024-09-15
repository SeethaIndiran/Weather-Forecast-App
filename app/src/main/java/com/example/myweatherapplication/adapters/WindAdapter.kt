package com.example.myweatherapplication.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapplication.databinding.ItemWindBinding
import com.example.myweatherapplication.models.Hour
import java.text.ParseException
import java.text.SimpleDateFormat

 class WindAdapter():RecyclerView.Adapter<WindAdapter.WeatherViewHolder>() {

    private var binding: ItemWindBinding? = null

    inner class WeatherViewHolder(itemBinding: ItemWindBinding) :
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
        binding = ItemWindBinding.inflate(
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

           // binding?.tvTemp?.text = hour.wind_kph.toString()
         //   binding?.tvTemp?.setTextColor(Color.parseColor("#FFFFFF"))
          //  binding?.tvTime?.setTextColor(Color.parseColor("#FFFFFF"))
           // Glide.with(this).load(hour.condition.icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding?.ivImage!!)
    //  binding?.ivImage?.setImageResource(hour.condition.icon.toInt())
            val input = SimpleDateFormat("yyyy-MM-dd hh:mm")
            val output = SimpleDateFormat("hh:mm aa")
            try{
                val date = input.parse(hour.time)
                val t = output.format(date!!)
                binding?.tvItemWind?.text = t

            }catch(e: ParseException){
                e.printStackTrace()

            }        }

      holder.setIsRecyclable(false)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}