package com.example.myweatherapplication

import java.io.InputStreamReader
import java.lang.StringBuilder

object Helper {

    fun readFileString(fileName:String):String{
        val inputStream = Helper::class.java.getResourceAsStream(fileName)
        val builder = StringBuilder()
        val reader = InputStreamReader(inputStream,"UTF-8")
        reader.readLines().forEach {
            builder.append(it)
        }
        return builder.toString()
    }
}