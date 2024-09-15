package com.example.myweatherapplication.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapplication.databinding.ItemLocationBinding
import com.example.myweatherapplication.ui.fragments.SearchFragment
import com.example.myweatherapplication.models.CityTown

class LocationAdapter(val fragment:Fragment):RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    private var binding:ItemLocationBinding? = null
    //private var list = ArrayList<CityTown>()

    inner class LocationViewHolder(itemBinding: ItemLocationBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object :
        DiffUtil.ItemCallback<CityTown>() {
        override fun areItemsTheSame(oldItem: CityTown, newItem: CityTown): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CityTown, newItem: CityTown): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        binding = ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationViewHolder(binding!!)

    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {

        val place = differ.currentList[position]

        holder.itemView.apply {
              binding?.tvLocation?.text = place.name
            setOnClickListener {
                if(fragment is SearchFragment){
                    fragment.setOnClick(place!!)
                }
            }

    binding?.ivDelete?.setOnClickListener {
        if(fragment is SearchFragment){
            fragment.deleteLocation(place,position)

        }

    }

                  }

        holder.setIsRecyclable(false)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    fun deleteItem(position: Int) {
      notifyDataSetChanged()
    }

}