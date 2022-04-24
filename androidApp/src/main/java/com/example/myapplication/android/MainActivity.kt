package com.example.myapplication.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.myapplication.RedmineApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    private val mainScope = MainScope()
    private var userId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvFirstName: TextView = findViewById(R.id.firstname_value)
        val tvLastName: TextView = findViewById(R.id.lastname_value)
        val tvMail: TextView = findViewById(R.id.mail_value)
        tvFirstName.text = "Loading..."
        mainScope.launch {
            kotlin.runCatching {
                RedmineApi().currentUser()
            }.onSuccess {
                tvFirstName.text = it.firstname
                tvLastName.text = it.lastname
                tvMail.text = it.mail
                userId = it.id
                getTimeEntries()
            }.onFailure {
                tvFirstName.text = "Error..."
                tvLastName.text = "Error..."
                tvMail.text = "Error..."
            }
        }

        val tvSpentTime: TextView = findViewById(R.id.spenttime_value)
        tvSpentTime.text = "Loading..."

    }

    fun getTimeEntries() {
        val tvSpentTime: TextView = findViewById(R.id.spenttime_value)
        var spentOnFilter: String = ""

        mainScope.launch {
            kotlin.runCatching {
                RedmineApi().timeEntries(userId, spentOnFilter)
            }.onSuccess {
                var hours: Double = 0.0
                for (timeEntry in it) {
                    hours += timeEntry.hours
                }
                tvSpentTime.text = BigDecimal(hours).setScale(2, RoundingMode.HALF_EVEN).toString() + " hours"
            }.onFailure {
                tvSpentTime.text = "Error..."
            }
        }
    }

}
