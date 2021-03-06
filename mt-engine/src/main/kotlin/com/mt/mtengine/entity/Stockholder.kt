package com.mt.mtengine.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import java.math.BigDecimal

/**
 * Created by gyh on 2020/3/7.
 */
@Table("mt_stockholder")
class Stockholder(
        @Id
        var id: Int? = null,
        /*** 用户id */
        var userId: Int? = null,
        /*** 该用户在公司的角色id */
        var roleId: Int? = null,
        /*** 公司id */
        var companyId: Int? = null,
        /*** 真实姓名 */
        var realName: String? = null,
        /*** 所在部门 */
        var department: String? = null,
        /*** 职位 */
        var position: String? = null,
        /*** 资金余额 */
        var money: BigDecimal? = null
) {


    override fun toString(): String {
        return "Role(id=$id, userId=$userId, roleId=$roleId, companyId=$companyId, name=$money)"
    }

    companion object {
        const val SUPER_ADMIN = "ROLE_SUPER_ADMIN"
        const val ANALYST = "ROLE_ANALYST"
        const val ADMIN = "ROLE_ADMIN"
        const val USER = "ROLE_USER"
    }

}