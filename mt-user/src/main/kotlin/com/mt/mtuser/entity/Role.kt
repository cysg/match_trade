package com.mt.mtuser.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority

/**
 * Created by gyh on 2020/3/7.
 */
@Table("mt_user_role")
class Role() : GrantedAuthority {
    @Id
    var id: Int? = null

    /**
     * 用户id
     */
    var userid: Int? = null

    /**
     * 该用户在公司的角色id
     */
    var roleid: Int? = null

    /**
     * 公司id
     */
    var companyid: Int? = null

    /**
     * 角色名
     */
    var name: String? = null

    /**
     * 角色中文名
     */
    var nameZh: String? = null


    constructor(userid: Int?, roleid: Int?, companyid: Int?) : this() {
        this.userid = userid
        this.roleid = roleid
        this.companyid = companyid
    }

    override fun toString(): String {
        return javaClass.simpleName +
                " [" +
                "Hash = " + hashCode() +
                ", id=" + id +
                ", userid=" + userid +
                ", roleid=" + roleid +
                ", companyid=" + companyid +
                "]"
    }

    override fun getAuthority(): String {
        return name!!
    }


}