package com.jx.Service;

import com.jx.common.ServerResponse;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface IOrderService {

    /**
     * 创建订单
     * */
    ServerResponse createOrder(Integer userId, Integer shippingId);

    //取消订单
    ServerResponse cancel(Integer userId,Long orderNo);

    //获取订单的商品信息
    ServerResponse get_order_cart_product(Integer userId);

    //订单列表
    ServerResponse list(Integer userId,Integer pageNum,Integer pageSize);

    //订单详情
    ServerResponse detail(Long orderNo);

    //支付接口
    ServerResponse alipay(Integer userId,Long orderNo);

    //支付宝回调接口
    ServerResponse alipay_callback(Map<String,String> map);

    //查询订单的支付状态
    ServerResponse query_order_pay_status(Long orderNo);
}
