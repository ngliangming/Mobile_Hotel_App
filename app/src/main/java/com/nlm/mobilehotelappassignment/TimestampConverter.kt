package com.nlm.mobilehotelappassignment

import android.util.Log

class TimestampConverter {
    fun intToTimestamp(day: Int, month: Int, year: Int): String {
        var timestamp: String = year.toString()

        timestamp += if (month.toString().length < 2) {
            "0$month"
        } else {
            "$month"
        }

        timestamp += if (day.toString().length < 2) {
            "0$day"
        } else {
            "$day"
        }

        Log.d("timestamp", timestamp)
        return timestamp
    }

    fun toDay(timestamp: String): Int {
//        Log.d("Day",timestamp.substring(6,8))
        return timestamp.substring(6, 8).toInt()
    }

    fun toMonth(timestamp: String): Int {
//        Log.d("Month",timestamp.substring(4,6))
        return timestamp.substring(4, 6).toInt()
    }

    fun toYear(timestamp: String): Int {
//        Log.d("Year",timestamp.substring(0,4))
        return timestamp.substring(0, 4).toInt()
    }

    fun daysBetween(timestamp1: String, timestamp2: String): Int {
        return getTotalDays(timestamp1) - getTotalDays(timestamp2)
    }

    fun getTotalDays(timestamp: String): Int {
        var days = toDay(timestamp) - 1
        var month = toMonth(timestamp) - 1
        var year = toYear(timestamp) - 1

        while (0 < year) {
            if (year % 4 == 0) {
//                Log.d("year", "${year.toString()} 1")
                days += 366
            } else {
//                Log.d("year", "${year.toString()} 0")
                days += 365
            }
            year--
        }

        if (month >= 1) {
            days += 31
        }
        if (month >= 2) {
            days += 28
            if (toYear(timestamp) % 4 == 0) {
                Log.d("year", toYear(timestamp).toString())
                days += 1
            }
        }
        if (month >= 3) {
            days += 31
        }
        if (month >= 4) {
            days += 30
        }
        if (month >= 5) {
            days += 31
        }
        if (month >= 6) {
            days += 30
        }
        if (month >= 7) {
            days += 31
        }
        if (month >= 8) {
            days += 31
        }
        if (month >= 9) {
            days += 30
        }
        if (month >= 10) {
            days += 31
        }
        if (month >= 11) {
            days += 30
        }

        return days
    }

}
