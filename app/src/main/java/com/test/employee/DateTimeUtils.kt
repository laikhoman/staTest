package com.test.employee

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    @SuppressLint("SimpleDateFormat")
    fun getDateFrom(formatString: String, strDate: String?): Date? {
        return if (!strDate.isNullOrEmpty() && strDate != "null") {
            val format = SimpleDateFormat(formatString, Locale.ENGLISH)
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.parse(strDate)
        } else {
            null
        }
    }

    @JvmStatic() fun getYYYYMMDDHHMMSS(date: Date?): String? {
        val format = "yyyy-MM-dd HH:mm:ss" // mention the format you need
        val sdf = SimpleDateFormat(format)
        return if (date != null) sdf.format(date)
        else null
    }

    @JvmStatic() fun getDDMMMYYYYFrom(date: Date?): String {
        val format = "dd-MMM-yyyy" // mention the format you need
        val sdf = SimpleDateFormat(format)
        return if (date != null) sdf.format(date)
        else "<Tanggal tidak valid>"
    }
}