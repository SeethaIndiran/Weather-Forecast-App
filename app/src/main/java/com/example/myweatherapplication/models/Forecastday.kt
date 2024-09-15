package com.example.myweatherapplication.models

data class Forecastday(
    val astro: Astro,
    val date: String,
    val date_epoch: Int,
    val day: Day,
    val hour: List<Hour>,
    var isExpandable:Boolean=false
)