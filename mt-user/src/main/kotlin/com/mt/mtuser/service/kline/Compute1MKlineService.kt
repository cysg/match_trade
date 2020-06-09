package com.mt.mtuser.service.kline

import com.mt.mtuser.dao.TradeInfoDao
import com.mt.mtuser.entity.Kline
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.awaitOne
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

/**
 * Created by gyh on 2020/6/7
 */
@Service
class Compute1MKlineService : ComputeKline() {
    override val tableName: String = "mt_1m_kline"
    val millisOfMinute = 60 * 1000L

    @Autowired
    private lateinit var connect: DatabaseClient

    @Autowired
    private lateinit var tradeInfoDao: TradeInfoDao

    override fun handlerRequest(time: Long): Boolean {
        return true
    }

    override fun formatDate(time: Long): Long {
        val c = Calendar.getInstance()
        c.timeInMillis = time
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    override suspend fun compute(stockId: Int, companyId: Int, time: Long): Kline {
        val startTime = Date(time - millisOfMinute) // 包括 :00
        val endTime = Date(time - 1)                // 不包括 :00
        val kline = selectMinuteKline(startTime, endTime, stockId)
        kline.stockId = stockId
        kline.companyId = companyId
        kline.time = Date(time)
        kline.openPrice = tradeInfoDao.findFirstPriceByTradeTimeAndStockId(startTime, endTime, stockId)
        kline.closePrice = tradeInfoDao.findLastPriceByTradeTimeAndStockId(startTime, endTime, stockId)
        return kline
    }

    override suspend fun getMinComputeTime(): Date? {
        return connect.execute("select min(trade_time) as tradeTime from mt_trade_info")
                .map { t, _ -> Optional.ofNullable(t.get("tradeTime", Date::class.java)) }
                .awaitOne().orElse(null)
    }

    override fun step(): Long = millisOfMinute

    suspend fun selectMinuteKline(startTime: Date, endTime: Date, stockId: Int): Kline {
        val kline = Kline()
        return connect.execute("select count(1) as tradesNumber," +
                " COALESCE(sum(trade_amount), 0) as tradesCapacity, " +
                " COALESCE(sum(trade_money), 0) as tradesVolume," +
                " COALESCE(avg(trade_price), 0) as avgPrice," +
                " COALESCE(min(trade_price), 0) as minPrice," +
                " COALESCE(max(trade_price), 0) as maxPrice" +
                " from ${TradeInfoDao.table} " +
                " where trade_time between :startTime and :endTime " +
                " and stock_id = :stockId ")
                .bind("startTime", startTime)
                .bind("endTime", endTime)
                .bind("stockId", stockId)
                .map { r, _ ->
                    kline.tradesNumber = r.get("tradesNumber", java.lang.Long::class.java)?.toLong()
                    kline.tradesCapacity = r.get("tradesCapacity", java.lang.Long::class.java)?.toLong()
                    kline.tradesVolume = r.get("tradesVolume", BigDecimal::class.java)
                    kline.avgPrice = r.get("avgPrice", BigDecimal::class.java)
                    kline.minPrice = r.get("minPrice", BigDecimal::class.java)
                    kline.maxPrice = r.get("maxPrice", BigDecimal::class.java)
                    kline
                }.awaitOne()
    }
}