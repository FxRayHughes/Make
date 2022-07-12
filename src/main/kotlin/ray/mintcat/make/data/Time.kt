package ray.mintcat.make.data

import java.lang.StringBuilder
import java.util.concurrent.TimeUnit

class Time(millisecond: Long) {
    var days: Long
    var hours: Long
    var minutes: Long
    var seconds: Long
    var milliseconds: Long

    init {
        days = TimeUnit.MILLISECONDS.toDays(millisecond)
        hours = TimeUnit.MILLISECONDS.toHours(millisecond) - days * 24L
        minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond) - TimeUnit.MILLISECONDS.toHours(millisecond) * 60L
        seconds = TimeUnit.MILLISECONDS.toSeconds(millisecond) - TimeUnit.MILLISECONDS.toMinutes(millisecond) * 60L
        milliseconds =
            TimeUnit.MILLISECONDS.toMillis(millisecond) - TimeUnit.MILLISECONDS.toSeconds(millisecond) * 1000L
    }

    fun toMilliseconds(): Long {
        return milliseconds + seconds * 1000L + minutes * 1000L * 60L + hours * 1000L * 60L * 60L + days * 1000L * 60L * 60L * 24L
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        if (days > 0) {
            stringBuilder.append("${days}天")
        }
        if (hours > 0) {
            stringBuilder.append("${hours}小时")
        }
        if (minutes > 0) {
            stringBuilder.append("${minutes}分")
        }
        if (seconds > 0) {
            stringBuilder.append("${seconds}秒")
        }
        if (stringBuilder.isEmpty()){
            stringBuilder.append("0秒")
        }
        return stringBuilder.toString()
    }
}