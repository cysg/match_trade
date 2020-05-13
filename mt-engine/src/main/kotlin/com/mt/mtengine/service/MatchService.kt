package com.mt.mtengine.service

import com.mt.mtcommon.OrderParam
import com.mt.mtcommon.TradeInfo
import com.mt.mtcommon.TradeState
import com.mt.mtengine.mq.MatchSink
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.lang.management.ThreadInfo
import java.math.BigDecimal

/**
 * Created by gyh on 2020/5/6.
 */
@Service
class MatchService {
    @Autowired
    private lateinit var sink: MatchSink

    @Autowired
    private lateinit var roomService: RoomService

    @Autowired
    private lateinit var positionsService: PositionsService

    @Autowired
    private lateinit var tradeInfoService: TradeInfoService

    @Autowired
    private lateinit var stockholderService: StockholderService

    @Autowired
    private lateinit var r2dbc: R2dbcService

    @Autowired
    private lateinit var redisUtil: RedisUtil

    fun onMatchSuccess(roomId: String, flag: String, buy: OrderParam, sell: OrderParam) = r2dbc.withTransaction {
        roomService.findCompanyIdByRoomId(roomId, flag)
                .flatMap { info ->
                    val threadInfo = TradeInfo(buy, sell, info.companyId, info.stockId, flag)
                    threadInfo.tradePrice = buy.price?.add(sell.price)?.divide(BigDecimal(2))
                    threadInfo.tradeMoney = threadInfo.tradePrice?.multiply(BigDecimal(threadInfo.tradeAmount ?: 0))
                    threadInfo.tradeState = TradeState.SUCCESS
                    buy.onTrade(threadInfo)
                    sell.onTrade(threadInfo)
                    positionsService.addAmount(info.companyId, info.stockId, buy.userId!!, threadInfo.tradeAmount!!)        // 添加买家的持股数
                            .flatMap { positionsService.minusAmount(info.companyId, info.stockId, sell.userId!!, threadInfo.tradeAmount!!) }// 减少卖价的持股数
                            .flatMap { stockholderService.addMoney(sell.userId!!, info.companyId, threadInfo.tradeMoney!!) }    // 添加卖家的钱
                            .flatMap { stockholderService.minusMoney(buy.userId!!, info.companyId, threadInfo.tradeMoney!!) }   // 减少买家的钱
                            .flatMap { tradeInfoService.save(threadInfo) }
                            .flatMap { redisUtil.updateUserOrder(buy) }
                            .flatMap { redisUtil.updateUserOrder(sell) }
                            .map { threadInfo }
                }.doOnSuccess { threadInfo -> sink.outTrade().send(MessageBuilder.withPayload(threadInfo).build()) }
                .doOnError { onMatchFailed(roomId, flag, buy, sell, it.message ?: "失败") }
    }

    fun onMatchFailed(roomId: String, flag: String, buy: OrderParam?, sell: OrderParam?, fileInfo: String) = r2dbc.withTransaction {
        roomService.findCompanyIdByRoomId(roomId, flag)
                .flatMap {
                    val threadInfo = TradeInfo(buy, sell, it.companyId, it.stockId, flag)
                    if (buy?.price != null && sell?.price != null)
                        threadInfo.tradePrice = buy.price?.add(sell.price)?.divide(BigDecimal(2))
                    threadInfo.tradeState = TradeState.FAILED
                    threadInfo.stateDetails = fileInfo
                    buy?.onTrade(threadInfo)
                    sell?.onTrade(threadInfo)
                    tradeInfoService.save(threadInfo)
                            .flatMap { buy?.let { b -> redisUtil.updateUserOrder(b) } }
                            .flatMap { sell?.let { s -> redisUtil.updateUserOrder(s) } }
                            .map { threadInfo }
                }.doOnSuccess { threadInfo -> sink.outTrade().send(MessageBuilder.withPayload(threadInfo).build()) }
                .doOnError { onMatchError(buy, sell, it.message ?: "失败") }
    }

    fun onMatchError(buy: OrderParam?, sell: OrderParam?, fileInfo: String): Mono<Void> {
        val buyResult = buy?.let {
            it.tradeState = TradeState.FAILED
            it.stateDetails = fileInfo
            redisUtil.updateUserOrder(it)
        } ?: Mono.empty()
        val sellResult = sell?.let {
            it.tradeState = TradeState.FAILED
            it.stateDetails = fileInfo
            redisUtil.updateUserOrder(it)
        } ?: Mono.empty()
        val threadInfo = TradeInfo(buyerId = buy?.userId, sellerId = sell?.userId)
        return buyResult.zipWith(sellResult)
                .doOnNext { sink.outTrade().send(MessageBuilder.withPayload(threadInfo).build()) }.then()
    }

}