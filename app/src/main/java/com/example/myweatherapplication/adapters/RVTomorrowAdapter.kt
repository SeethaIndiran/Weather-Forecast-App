package com.example.myweatherapplication.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapplication.R
import com.example.myweatherapplication.databinding.RvItemTomorrowBinding
import com.example.myweatherapplication.models.Hour
import java.text.ParseException
import java.text.SimpleDateFormat

class RVTomorrowAdapter():RecyclerView.Adapter<RVTomorrowAdapter.WeatherViewHolder>() {

    private var binding: RvItemTomorrowBinding? = null

    inner class WeatherViewHolder(itemBinding: RvItemTomorrowBinding) :
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
        binding = RvItemTomorrowBinding.inflate(
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

          //  binding?.tvTemp?.text = hour.temp_c.toString() + "°"
           // Glide.with(this).load(hour.condition.icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(binding?.ivImage!!)
            //binding?.ivImage?.setImageResource(R.drawable.sunnylotte)
            //Picasso.get().load(hour.condition.icon).into(binding?.ivImage!!)
              setImage(hour.condition.code,hour.is_day)

            val input = SimpleDateFormat("yyyy-MM-dd hh:mm")
            val output = SimpleDateFormat("hh:mm aa")
            try{
                val date = input.parse(hour.time)
                val t = output.format(date!!)
                binding?.tvTime?.text = t
            }catch(e: ParseException){
                e.printStackTrace()

            }        }

      //  if(hour.time == )

    holder.setIsRecyclable(false)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setImage(code:Int,isDay:Int){

        when(code){
            in 1001..1007->{
                if(isDay == 1){
                    binding?.ivImage?.setAnimation(R.raw.partly_cloudy)
                    binding?.ivImage?.playAnimation()
                }else {
                    binding?.ivImage?.setAnimation(R.raw.moon)
                    binding?.ivImage?.playAnimation()
                }
            }

            in 1008..1010->{
                binding?.ivImage?.setAnimation(R.raw.full_cloudy)
                binding?.ivImage?.playAnimation()
            }
            in 1051..1270->{
                if(isDay == 1){
                    binding?.ivImage?.setAnimation(R.raw.cloudyrain)
                    binding?.ivImage?.playAnimation()
                }else {
                    binding?.ivImage?.setAnimation(R.raw.rainynight)
                    binding?.ivImage?.playAnimation()
                }
            }

            1000->{

                if(isDay == 1){
                    binding?.ivImage?.setAnimation(R.raw.ssunny)
                    binding?.ivImage?.playAnimation()
                }else {
                    binding?.ivImage?.setAnimation(R.raw.moony)
                    binding?.ivImage?.playAnimation()
                }
            }else->{
            binding?.ivImage?.setAnimation(R.raw.partly_cloudy)
            binding?.ivImage?.playAnimation()
            }
        }
    }



}