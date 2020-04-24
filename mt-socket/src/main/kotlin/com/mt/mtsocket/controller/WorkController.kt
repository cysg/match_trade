package com.mt.mtsocket.controller

import com.mt.mtsocket.entity.OrderParam
import com.mt.mtsocket.entity.ResponseInfo
import com.mt.mtsocket.service.WorkService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import reactor.core.publisher.Mono

/**
 * Created by gyh on 2020/4/17.
 */
@Controller
class WorkController {
    @Autowired
    private lateinit var workService: WorkService

    /**
     * @api {connect} /echo 测试接口
     * @apiDescription  测试接口，该接口的[value]字段传什么就放回什么
     * @apiName echo
     * @apiParam {String} value 任意字符
     * @apiVersion 0.0.1
     * @apiParamExample {json} 请求-例子:
     * {"order":"/echo", "data": {"value": "123"}, "req":12}
     * @apiSuccessExample {json} 成功返回:
     * {"data":{"code":0,"msg":"成功","data":{"value":"123"}},"req":12}
     * @apiGroup Socket
     * @apiPermission none
     */
    @RequestMapping("/echo")
    fun echo(@RequestParam value: String, @RequestParam(required = false) nmka: Any?): Mono<ResponseInfo<Map<String, String>>> {
        return ResponseInfo.ok(Mono.just(mapOf("value" to value)))
    }

    /**
     * @api {connect} /offer 报价
     * @apiDescription 报价
     * @apiName offer
     * @apiVersion 0.0.1
     * @apiUse OrderParam
     * @apiSuccessExample {json} 成功返回:
     * {"data":{"code":0,"msg":"成功","data":null},"req":12}
     * @apiGroup Socket
     * @apiPermission none
     */
    @RequestMapping("/offer")
    fun offer(@RequestBody orderParam: OrderParam): Mono<ResponseInfo<String>> {
        return ResponseInfo.ok(workService.offer(orderParam))
    }
}