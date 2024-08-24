package com.devik.homebarordermanager.util

import android.content.res.Resources
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatUtil {

    private val currentLocale: Locale
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources.getSystem().configuration.locales.get(0)
            } else {
                Resources.getSystem().configuration.locale
            }
        }

    const val DATE_YEAR_MONTH_DAY_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
    private const val TIME_SECOND_PATTERN = "HH:mm"

    fun getTime(dateTime: String): String {
        val inputFormat = SimpleDateFormat(DATE_YEAR_MONTH_DAY_TIME_PATTERN, currentLocale)
        val outputFormat = SimpleDateFormat(TIME_SECOND_PATTERN, currentLocale)
        val date: Date = inputFormat.parse(dateTime)

        return outputFormat.format(date)
    }
}