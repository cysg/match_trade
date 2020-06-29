package com.mt.mtuser

/**
 * Created by gyh on 2020/3/26.
 */

import com.mt.mtcommon.plus
import com.mt.mtcommon.toEpochMilli
import com.mt.mtcommon.toLocalDateTime
import com.mt.mtcommon.toMillisOfDay
import com.mt.mtuser.common.Util
import com.mt.mtuser.entity.page.PageQuery
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import java.sql.Time
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.system.measureTimeMillis


fun main1() = runBlocking<Unit> {
    val time = measureTimeMillis {
        println("The answer is ${concurrentSum()}")
    }
    println("Completed in $time ms")
    println(Thread.currentThread().name)
}

suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    println(one.await())
    one.await() + two.await()
}

suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // 假设我们在这里做了些有用的事
    println(Thread.currentThread().name)
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // 假设我们在这里也做了些有用的事
    println(Thread.currentThread().name)
    return 29
}

fun main2() = runBlocking<Unit> {
    launch { // 运行在父协程的上下文中，即 runBlocking 主协程
        println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Unconfined) { // 不受限的——将工作在主线程中
        println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Default) { // 将会获取默认调度器
        println("Default               : I'm working in thread ${Thread.currentThread().name}")
    }
    launch(newSingleThreadContext("MyOwnThread")) { // 将使它获得一个新的线程
        println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
    }
    GlobalScope.launch {
        println("GlobalScope               : I'm working in thread ${Thread.currentThread().name}")
    }
}

fun testTime() {
    val time = Time.valueOf("08:00:00")
    println(time.toLocalTime().toSecondOfDay())
    val localTime = LocalTime.parse("00:00:01")
    println(localTime.toMillisOfDay())
    println(localTime + LocalTime.parse("00:00:01"))
}

fun foo(): Flow<Int> = flow { // 流构建器
    for (i in 1..3) {
        delay(100) // 假装我们在这里做了一些有用的事情
        println("nmka")
        emit(i) // 发送下一个值
    }
}

fun main3() = runBlocking<Unit> {
    val time = measureTimeMillis {
        val buf = foo().buffer() // 缓冲发射项，无需等待
        buf.collect { value ->
            delay(300) // 假装我们花费 300 毫秒来处理它
            println(value)
        }
        buf.collect { value ->
            delay(300) // 假装我们花费 300 毫秒来处理它
            println(value)
        }
    }
    println("Collected in $time ms")
}

fun main4() {
    val time = System.currentTimeMillis()
    val c = Calendar.getInstance()
    c.timeInMillis = Util.encoderDate("2020-06-9 15:00:00")
    c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) / 15 * 15)    // 15的倍数
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)

    println(c.timeInMillis)
    println(c.get(Calendar.MINUTE) % 15 == 0)
    println(Date(c.timeInMillis))

    for (i in 0..5 step 2) {
        println(i)
    }

    println()
    val date = Date(1591340280000)
    println(date)
    println(Date(1589504123171))
}

// 模仿事件流
fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

fun main5() = runBlocking {
    events().collect { event ->
        launch(Dispatchers.Default) {
            delay(1000)
            println("Event: $event ${Thread.currentThread().name}")
        }
    }
    println("Done")
}

fun main6() = runBlocking {
    val list = events().toList()
    val count = CountDownLatch(list.size)
    list.forEach { event ->
        launch(Dispatchers.Default) {
            delay(1000)
            println("Event: $event ${Thread.currentThread().name}")
            count.countDown()
        }
    }
    println("Done")
    count.await()
    println("Done")
}

fun main7() {
    val page = PageQuery(pageNum = 10, pageSize = 10)
    println(page.where().toString())
    println(page.toPageSql())
    println(BigDecimal("5.5").divide(BigDecimal("45.5"), 4, BigDecimal.ROUND_HALF_EVEN).toPlainString())
    println(BigDecimal("0.0").compareTo(BigDecimal(0)) == 0)
    println(Date(1592233200000))
}

fun main8() {
    val formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s.SSS")
    val formattedDateTime = LocalDateTime.parse("2020-12-2 2:3:4", formatter)
    val instant = Instant.ofEpochMilli(System.currentTimeMillis())
    val time = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    println(formattedDateTime)
    println(time)
    val systemZone = ZoneId.systemDefault() // my timezone
    val currentOffsetForMyZone = systemZone.rules.getOffset(instant)
    println(currentOffsetForMyZone)
    println(LocalDateTime.ofEpochSecond(System.currentTimeMillis() / 1000, 0, ZonedDateTime.now(ZoneId.systemDefault()).offset))
    println(LocalDateTime.of(LocalDate.now(), LocalTime.MIN))
    println(LocalDateTime.of(LocalDate.now(), LocalTime.MAX))
    println(LocalDateTime.now().toInstant(currentOffsetForMyZone).toEpochMilli())
    println(System.currentTimeMillis())
    println(LocalDate.now().atStartOfDay())
    println(LocalDateTime.now().withYear(2019).withNano(123000000).withMinute(57 / 15 * 15))
    println(LocalDate.now().withDayOfMonth(1).atStartOfDay())
    println(LocalDate.of(2020, 2, 1).with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay())
    println(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
    println(LocalTime.MIN.toLocalDateTime().toEpochMilli())
}

fun main() = main8()
