package com.mt.mtuser.controller

import com.mt.mtuser.common.Util
import com.mt.mtuser.entity.BaseUser
import com.mt.mtuser.entity.ResponseInfo
import com.mt.mtuser.entity.Role
import com.mt.mtuser.entity.User
import com.mt.mtuser.service.RoleService
import com.mt.mtuser.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

/**
 * Created by gyh on 2020/3/19.
 */
@RestController
@RequestMapping("/user")
class UserController {
    @Autowired
    private lateinit var userService: UserService
    @Autowired
    private lateinit var roleService: RoleService

    /**
     * @api {put} /user/info 获取用户的信息
     * @apiDescription  获取用户的信息
     * @apiName getUserInfo
     * @apiVersion 0.0.1
     * @apiParam {Integer} [id] 用户id,不传默认获取自己的信息
     * @apiSuccessExample {json} 成功返回:
     * {"code": 0, "msg": "成功", "data": {"roles": [{"id": 33,"userid": 11,"roleid": 3,"companyid": null,"name":
     * "ROLE_USER","nameZh": "股东","authority": "ROLE_USER"}],"id": 11,"phone": "2222","nickName": null,"idNum": null,
     * "userPhoto": null,"createTime": "2020-03-07T20:54:35.000+0000","lastTime": null,"updateTime": "2020-03-07T20:54:35.000+0000",
     * "username": "2222"}}
     * @apiError UserNotFound The `id` of the User was not found.
     * @apiGroup User
     * @apiPermission user
     */
    @GetMapping("/info")
    fun getUserInfo(@RequestParam(required = false) id: Int?): Mono<ResponseInfo<User>> {
        return ResponseInfo.ok(if (id == null) {
            BaseUser.getcurrentUser()
                    .flatMap { userService.findById(it.id!!) }
                    .flatMap { user ->
                        roleService.selectRolesByUserId(user.id!!).map { user.roles = listOf(it); user }
                    }
        } else {
            userService.findById(id)
        })
    }

    /**
     * @api {put} /user 修改用户的信息
     * @apiDescription  修改用户的信息,不会修改用户的`id`，`phone`
     * @apiName alter
     * @apiVersion 0.0.1
     * @apiParamExample {json} 请求-例子:
     * {"nickName":"nmka"}
     * @apiUse User
     * @apiSuccessExample {json} 成功返回:
     * {"code":0,"msg":"修改成功","data":null}
     * @apiGroup User
     * @apiPermission user
     */
    @PutMapping
    fun alter(@RequestBody user: User): Mono<ResponseInfo<User>> {
        return if (!Util.isEmpty(user)) {
            ResponseInfo.ok(BaseUser.getcurrentUser().flatMap {
                user.id = it.id
                user.phone = null
                user.passwordEncoder()
                userService.save(user)
            }, "修改成功")
        } else {
            ResponseInfo.failed(Mono.empty(), "请至少更新一个属性")
        }
    }

    /**
     * @api {put} /user/role 修改用户的角色和所属公司
     * @apiDescription  修改角色的信息
     * @apiName changeAuthority
     * @apiVersion 0.0.1
     * @apiParamExample {json} 请求-例子:
     * {"userid":1, "roleid":2, "companyid": 3}
     * @apiParam {Integer} userid 用户id
     * @apiParam {Integer} [roleid] 角色id
     * @apiParam {Integer} [companyid] 公司id
     * @apiSuccessExample {json} 成功返回:
     * {"code":0,"msg":"修改成功","data":null}
     * @apiGroup User
     * @apiPermission admin
     */
    @PutMapping("/role")
    @PreAuthorize("hasRole('ADMIN')")
    fun changeAuthority(@RequestBody role: Role): Mono<ResponseInfo<Role>> {
        return ResponseInfo.ok(roleService.save(role), "修改成功")
    }

}