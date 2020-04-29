package com.mt.mtuser.entity

import com.mt.mtcommon.RoomEnum
import com.mt.mtcommon.RoomRecord
import com.mt.mtuser.common.minus
import com.mt.mtuser.entity.room.BaseRoom
import com.mt.mtuser.entity.room.TimingMatch
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.sql.Time
import java.time.LocalTime
import java.util.*

/**
 * Created by gyh on 2020/3/26.
 * 房间启用记录
 * @apiDefine ClickMatch
 * @apiParam {Integer} id
 * @apiParam {Integer} roomId 房间id
 * @apiParam {String} model 模式对应撮合模式
 * @apiParam {Integer} stockId 股票id
 * @apiParam {Integer} companyId 公司id
 * @apiParam {Date} startTime 启用时间
 * @apiParam {Date} endTime 结束时间
 */
@Table("mt_room_record ")
class RoomRecordEntity(
        @Id var id: Int? = null,
        roomId: String? = null,     // 房间id，四张房间表唯一
        model: String? = null,      // 模式对应撮合模式
        stockId: Int? = null,       // 股票id
        companyId: Int? = null,     // 公司id
        startTime: Date? = null,    // 启用时间
        endTime: Date? = null,      // 结束时间
        cycle: LocalTime? = null,   // 周期
        duration: LocalTime? = null  // 时长
) : RoomRecord(roomId, model, stockId, companyId, startTime, endTime, cycle, duration) {

    constructor(room: BaseRoom) : this(
            roomId = room.roomId,
            model = room.flag,
            companyId = room.companyId,
            duration = room.time) {
        cycle = when (room.roomId!![0].toString()) {
            RoomEnum.CLICK.flag ->  LocalTime.MIN
            RoomEnum.BICKER.flag -> LocalTime.ofSecondOfDay(1)
            RoomEnum.DOUBLE.flag -> LocalTime.ofSecondOfDay(1)
            RoomEnum.TIMELY.flag -> LocalTime.ofSecondOfDay(1)
            RoomEnum.TIMING.flag -> (room as TimingMatch).matchTime
            else -> throw IllegalStateException("不支持的房间号模式${roomId}")
        }
    }


    override fun computingTime(): RoomRecordEntity {
        duration = startTime?.let { start ->
            endTime?.let { end ->
                LocalTime.ofSecondOfDay((start - end) / 1000)
            }
        }
        return this
    }
}
