package com.mt.mtcommon

import java.time.Duration
import java.time.LocalTime
import java.util.*

/**
 * Created by gyh on 2020/3/25.
 * 所有的扩展函数和运算符的重载都统一放到该文件
 */


/**
 * 把long转Date，long是毫秒值
 */
fun Long.toDate(): Date = Date(this)

/**
 * 返回LocalTime表示的毫秒值,注意可能的精度损失
 */
fun LocalTime.toMillisOfDay(): Long = this.toNanoOfDay() / 1000_000

/**
 * 把LocalTime转换为Duration
 */
fun LocalTime.toDuration(): Duration = Duration.ofNanos(this.toNanoOfDay())

/**
 * 把LocalTime转换为Date
 */
fun LocalTime.toDate(): Date = Date(System.currentTimeMillis() - LocalTime.now().toMillisOfDay() + this.toMillisOfDay())

/**
 * 获取本月的第一天
 */
fun minDay(): Date {
    val c = Calendar.getInstance()
    c.set(Calendar.DAY_OF_MONTH, 1)
    return c.time
}

/**
 * 获取本月的最后一天
 */
fun maxDay(): Date {
    val ca = Calendar.getInstance()
    ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH))
    return ca.time
}

/**
 * LocalTime 加 运算符
 */
operator fun LocalTime.plus(other: LocalTime): LocalTime {
    return if (this.toNanoOfDay() + other.toNanoOfDay() > LocalTime.MAX.toNanoOfDay()) {
        LocalTime.MAX
    } else this.plusNanos(other.toNanoOfDay())
}

/**
 * 重载Date的[减]运算符
 */
operator fun Date.minus(startTime: Date): Long {
    return this.time - startTime.time
}