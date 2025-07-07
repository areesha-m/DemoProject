package com.example.myprofileapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateFormatterUtil @Inject constructor() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun format(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val day = date.dayOfMonth
            val suffix = when {
                day in 11..13 -> "th"
                day % 10 == 1 -> "st"
                day % 10 == 2 -> "nd"
                day % 10 == 3 -> "rd"
                else -> "th"
            }
            val monthYear = date.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH))
            "$day$suffix $monthYear"
        } catch (e: Exception) {
            dateString
        }
    }
}
