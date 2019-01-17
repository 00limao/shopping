package com.jx.Controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.jx.Service.IOrderService;
import com.jx.common.Const;
import com.jx.common.ServerResponse;
import com.jx.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping(value = "/order")
public class OrderController {

    @Autowired
    IOrderService orderService;
    //创建订单
    @RequestMapping(value = "/create.do")
    public ServerResponse createOrder(HttpSession session,Integer shippingId){
        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByError("需要登录");
        }
        return orderService.createOrder(userInfo.getId(),shippingId);
    }

    //取消订单
    @RequestMapping(value = "/cancel.do")
    public ServerResponse cancel(HttpSession session,Long orderNo){
        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByError("需要登录");
        }
        return orderService.cancel(userInfo.getId(),orderNo);
    }


    //获取订单的商品信息
    @RequestMapping(value = "/get_order_cart_product.do")
    public ServerResponse get_order_cart_product(HttpSession session){
        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByError("需要登录");
        }
        return orderService.get_order_cart_product(userInfo.getId());
    }

    //订单list
    @RequestMapping(value = "/list.do")
    public ServerResponse list(HttpSession session,
                               @RequestParam(required = false,defaultValue = "1")Integer pageNum,
                               @RequestParam(required = false,defaultValue = "10")Integer pageSize){
        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByError("需要登录");
        }
        return orderService.list(userInfo.getId(),pageNum,pageSize);
    }

    //订单详情
    @RequestMapping(value = "/detail.do")
    public ServerResponse detail(HttpSession session,Long orderNo){
        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByError("需要登录");
        }
        return orderService.detail(orderNo);
    }

    //支付接口
    @RequestMapping(value = "/pay.do")
    public ServerResponse pay(HttpSession session,Long orderNo){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByError("需要登录");
        }
        return orderService.alipay(userInfo.getId(),orderNo);
    }

    //支付宝服务器回调应用服务器接口
    @RequestMapping(value = "/alipay_callback.do")
    public ServerResponse callback(HttpServletRequest request){

        System.out.println("====支付宝服务器回调应用服务器接口");
//        UserInfo userInfo=(UserInfo)session.getAttribute(Const.CURRENTUSER);
//        if(userInfo==null){
//            return ServerResponse.createServerResponseByError(Const.ReponseCodeEnum.NEED_LOGIN.getCode(),
//                    Const.ReponseCodeEnum.NEED_LOGIN.getDesc());
//        }
        Map<String,String[]> params = request.getParameterMap();
        Map<String,String> requestparams= Maps.newHashMap();
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext()){
            String key = it.next();
            String[] strArr = params.get(key);
            String value = "";
            for(int i=0;i<strArr.length;i++){
                value = (i==strArr.length-1)?value+strArr[i]:value+strArr[i]+",";
                System.out.println(key+":"+value);
            }
            requestparams.put(key,value);
        }
        //支付宝验签
        try {
//            System.out.println("验签前。。。。");
            requestparams.remove("sign_type");
//            System.out.println("验签中。。。。");
            boolean result = AlipaySignature.rsaCheckV2(requestparams,Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
//            System.out.println("验签结果："+result);
            if(result==false){
                return ServerResponse.createServerResponseByError("非法请求，验证不通过");
            }
            //处理业务逻辑
            return orderService.alipay_callback(requestparams);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
       return null;
    }

    //查询订单的支付状态
    @RequestMapping(value = "/query_order_pay_status.do")
    public ServerResponse query_order_pay_status(HttpSession session,Long orderNo){
        UserInfo userInfo = (UserInfo)session.getAttribute(Const.CURRENTUSER);
        if(userInfo==null){
            return ServerResponse.createServerResponseByError("需要登录");
        }
        return orderService.query_order_pay_status(orderNo);
    }

}