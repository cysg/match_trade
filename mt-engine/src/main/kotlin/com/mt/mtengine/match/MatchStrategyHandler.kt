package com.mt.mtengine.match

import com.mt.mtengine.match.strategy.MatchStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.support.ApplicationObjectSupport
import org.springframework.stereotype.Component

/**
 * Created by gyh on 2020/5/2.
 */
@Component
class MatchStrategyHandler : ApplicationObjectSupport(), InitializingBean {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * 加载策略
     */
    override fun afterPropertiesSet() {
        obtainApplicationContext().getBeansOfType(MatchStrategy::class.java)
                .values.forEach { MatchManager.register(it) }
    }

}