package com.example.myapplication

import com.example.myapplication.model.TimeEntriesResponse
import com.example.myapplication.model.TimeEntry
import com.example.myapplication.model.User
import com.example.myapplication.model.UserResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.*
import kotlinx.serialization.json.Json


class RedmineApi {
    private val apiKey = "PASTE YOUR API KEY HERE"
    private val baseUrl = "https://redmine.e2e4gu.ru"
    private val client = HttpClient() {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun currentUser(): User {
        val userResponse: UserResponse = client.get(baseUrl + "/users/current.json") {
            headers {
                append("X-Redmine-API-Key", apiKey)
            }
        }.body()
        return userResponse.user
    }

    suspend fun timeEntries(userId: Int, _spentOnFilter: String): Array<TimeEntry> {
        var spentOnFilter = _spentOnFilter
        if (spentOnFilter.equals("")) {
            spentOnFilter = buildDefaultSpentOnFilter()
        }
        val response: TimeEntriesResponse = client.get(baseUrl + "/time_entries.json") {
            headers {
                //append(HttpHeaders.ContentType, "application/json")
                append("X-Redmine-API-Key", apiKey)
            }
            parameter("user_id", userId)
            parameter("limit", 10000)
            parameter("spent_on", spentOnFilter)
        }.body()
        return response.time_entries
    }

    fun buildDefaultSpentOnFilter(): String {
        val now = Clock.System.now()
        val systemTZ = TimeZone.currentSystemDefault()
        val yesterday = now.minus(1, DateTimeUnit.DAY, systemTZ)
        val firstDayOfMonth = now.minus(now.toLocalDateTime(systemTZ).dayOfMonth-1, DateTimeUnit.DAY, systemTZ)
        var firstDayOfMonthString = firstDayOfMonth.toLocalDateTime(systemTZ).monthNumber.toString()
        if (firstDayOfMonthString.length < 2) {
            firstDayOfMonthString = "0" + firstDayOfMonthString;
        }
        var yesterdayMonthString = yesterday.toLocalDateTime(systemTZ).monthNumber.toString()
        if (yesterdayMonthString.length < 2) {
            yesterdayMonthString = "0" + yesterdayMonthString;
        }
        var firstDayOfMonthDayString = firstDayOfMonth.toLocalDateTime(systemTZ).dayOfMonth.toString()
        if (firstDayOfMonthDayString.length < 2) {
            firstDayOfMonthDayString = "0" + firstDayOfMonthDayString;
        }
        var yesterdayDayString = yesterday.toLocalDateTime(systemTZ).dayOfMonth.toString()
        if (yesterdayDayString.length < 2) {
            yesterdayDayString = "0" + yesterdayDayString;
        }
        var spentOnPattern = (("><"
                + firstDayOfMonth.toLocalDateTime(systemTZ).year.toString() + "-"
                + firstDayOfMonthString + "-"
                + firstDayOfMonthDayString
                ) + "|"
                + yesterday.toLocalDateTime(systemTZ).year.toString() + "-"
                + yesterdayMonthString + "-"
                + yesterdayDayString)

        return spentOnPattern
    }
}