package com.jx.Service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.jx.Service.IAddressService;
import com.jx.common.ServerResponse;
import com.jx.dao.ShippingMapper;
import com.jx.pojo.Shipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        //参数校验
        if(shipping==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //添加
        shipping.setUserId(userId);
        shippingMapper.insert(shipping);
        //返回结果
        Map<String,Integer> map = Maps.newHashMap();
        map.put("shoppingId",shipping.getId());
        return ServerResponse.createServerResponseBySuccess(map);
    }

    @Override
    public ServerResponse del(Integer userId, Integer shippingId) {
        //参数校验
        if(shippingId==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //删除
        int result = shippingMapper.deleteByUserIdShippingId(userId, shippingId);
        //返回结果
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError("删除失败");
    }

    @Override
    public ServerResponse update(Shipping shipping) {
        //非空判断
        if(shipping==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //更新
        int result = shippingMapper.updateBySelectiveKey(shipping);
        //返回结果
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError("更新失败");
    }

    @Override
    public ServerResponse select(Integer shippingId) {
        //非空判断
        if(shippingId==null){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //查询
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        //返回结果
        return ServerResponse.createServerResponseBySuccess(shipping);
    }

    @Override
    public ServerResponse list(Integer userId,Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList=shippingMapper.seleceByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);

        return ServerResponse.createServerResponseBySuccess(pageInfo);
    }


}
