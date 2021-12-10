package com.example.tama.ui.events

data class Event (
    val title: String,
    val date: String,

    val startTime: String,
    private val endTime: String,

    val time: String = "$startTime - $endTime"
)