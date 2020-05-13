package com.mt.mtuser.dao

import com.mt.mtuser.entity.Stockholder
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 * Created by gyh on 2020/3/17.
 */
interface StockholderDao: CoroutineCrudRepository<Stockholder, Int> {
    @Query("select" +
            " ur.id, user_id, role_id, company_id, name, name_zh" +
            " from mt_stockholder ur" +
            " left join mt_role r on ur.role_id = r.id" +
            " where user_id = $1")
    fun selectRolesByUserId(userId: Int): Flow<Stockholder>

    @Modifying
    @Query("insert into mt_stockholder(user_id, role_id, company_id) values(:userId, :roleId, :companyId)")
    suspend fun save(userId: Int, roleId: Int, companyId: Int): Int

    @Query("select count(*) from mt_stockholder where user_id = :userId and role_id = :roleId and company_id = :companyId limit 1")
    suspend fun exists(userId: Int, roleId: Int, companyId : Int): Int

    @Query("select * from mt_stockholder where user_id = :userId and role_id = :roleId and company_id = :companyId")
    suspend fun find(userId: Int, roleId: Int, companyId : Int): Stockholder?

    @Query("select * from mt_stockholder where user_id = :userId and company_id = :companyId")
    suspend fun findByUserIdAndCompanyId(userId: Int, companyId : Int): Stockholder?

    @Query("select * from mt_stockholder where user_id = :userId and role_id = :roleId")
    suspend fun findByUserIdAndRoleId(userId: Int, roleId: Int): Stockholder?

    @Query("select * from mt_stockholder where company_id = :companyId")
    fun findByCompanyId(companyId: Int): Flow<Stockholder>
}