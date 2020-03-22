package com.mt.mtuser.dao

import com.mt.mtuser.entity.Stock
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

/**
 * Created by gyh on 2020/3/22.
 */
interface StockDao: CoroutineCrudRepository<Stock, Int> {
}