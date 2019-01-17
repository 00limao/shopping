package com.jx.Service;

import com.jx.common.ServerResponse;
import org.springframework.stereotype.Service;

@Service
public interface ICartService {

    public ServerResponse add(Integer userId,Integer productId,Integer count);

    ServerResponse list(Integer userId);

    ServerResponse update(Integer userId,Integer productId,Integer count);

    ServerResponse delete_product(Integer userId,String productIds);

    ServerResponse select(Integer userId,Integer productId,Integer check);

    ServerResponse get_cart_product_count(Integer userId);
}
