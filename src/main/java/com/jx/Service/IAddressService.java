package com.jx.Service;

import com.jx.common.ServerResponse;
import com.jx.pojo.Shipping;

public interface IAddressService {

    //添加收获地址
    public ServerResponse add(Integer userId, Shipping shipping);

    //删除地址
    ServerResponse del(Integer userId,Integer shippingId);

    //更新地址
    ServerResponse update(Shipping shipping);

    //查看地址
    ServerResponse select(Integer shippingId);

    //分页查询
    ServerResponse list(Integer userId,Integer pageNum,Integer pageSize);

}
