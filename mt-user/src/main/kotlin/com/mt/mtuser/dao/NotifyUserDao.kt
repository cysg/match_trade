package com.mt.mtuser.dao

import com.mt.mtuser.entity.NotifyUser
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

/**
 * Created by gyh on 2020/5/26.
 */
interface NotifyUserDao : CoroutineCrudRepository<NotifyUser, Int> {

    @Query("select count(1) from mt_notify_user where user_id = :userID and status = 'unread'")
    suspend fun countByUnread(userId: Int): Long

    @Query("select * from mt_notify_user where user_id = :userID")
    fun findAllByUserId(userId: Int): Flow<NotifyUser>

    @Modifying
    @Query("update mt_notify_user set status = :status where id in (:id)")
    suspend fun setStatusById(id: Iterable<Int>, status: String): Int
}