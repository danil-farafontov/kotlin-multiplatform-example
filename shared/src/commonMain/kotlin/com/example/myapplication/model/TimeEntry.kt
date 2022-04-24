package com.example.myapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class TimeEntriesResponse(val time_entries: Array<TimeEntry>)

@Serializable
data class TimeEntry(val id: Int, val hours: Double)