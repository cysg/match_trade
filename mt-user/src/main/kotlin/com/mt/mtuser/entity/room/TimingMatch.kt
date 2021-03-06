package com.mt.mtuser.entity.room

import com.mt.mtcommon.RoomEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * Created by gyh on 2020/3/23.
 * 定时撮合
 * @apiDefine TimingMatch
 * @apiParam {Integer} companyId 公司id
 * @apiParam {Integer} stockId 股票id
 * @apiParam {String} name 房间名字
 * @apiParam {String} time 时长 格式：HH:mm:SS
 * @apiParam {String} startTime: 房间开启时间 格式：HH:mm:SS
 * @apiParam {Integer} numberTrades 单笔交易数量
 * @apiParam {Double} lowScope 报价最低值
 * @apiParam {Double} highScope 报价最高值
 * @apiParam {String} [oldFlag]=I 旧房间的标识符
 */
@Table("mt_room_timing")
class TimingMatch(
        @Id
        override var roomId: String? = null,    // 房间id，四张房间表唯一
        override var companyId: Int? = null,    // 公司id
        override var stockId: Int? = null,      // 股票id
        override var name: String? = null,      // 房间名字
        override var people: Int? = null,       // 人数
        override var time: LocalTime? = null,     // 时长
        override var startTime: LocalTime? = null,  // 房间开启时间
        override var numberTrades: Int? = null,  // 单笔交易数量
        override var lowScope: Double? = null,  // 最低报价
        override var highScope: Double? = null, // 最高报价
        override var enable: String? = null,    // 是否启用（0：关闭，1：开启）
        override var createTime: LocalDateTime? = null  // 创建时间
) : BaseRoom {
    override val flag: String = RoomEnum.TIMING.mode
    @org.springframework.data.annotation.Transient
    var oldFlag: String? = null             // 标识符,更改房间时用
    override suspend fun validNull() {
        people = null
    }
}
