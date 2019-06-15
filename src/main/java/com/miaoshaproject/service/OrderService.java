package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.OrderModel;

/**
 * @author aixscode.github.io
 * @date 2019/1/12 10:34
 */
public interface OrderService {


    //1.通过前端url上传过来的秒杀活动id,然后在下单接口校验对应id是否属于对应商品且活动已开始
    OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer id) throws BussinessException;



}
