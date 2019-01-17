package com.jx.Service.impl;

import com.google.common.collect.Lists;
import com.jx.Service.ICartService;
import com.jx.common.Const;
import com.jx.common.ServerResponse;
import com.jx.dao.CartMapper;
import com.jx.dao.ProductMapper;
import com.jx.pojo.Cart;
import com.jx.pojo.Product;
import com.jx.utils.BigDecimalUtils;
import com.jx.vo.CartProductVO;
import com.jx.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
@Component
public class CartServiceImpl implements ICartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Override
    public ServerResponse add(Integer userId,Integer productId, Integer count) {

        //1，参数非空校验
        if(productId==null || count == null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createServerResponseByError("要添加的商品不存在");
        }
        //2,根据productId和userId查询购物信息
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart==null){
            //添加
            Cart cart1 = new Cart();
            cart1.setUserId(userId);
            cart1.setProductId(productId);
            cart1.setQuantity(count);
            cart1.setChecked(Const.CartCheckedEeum.PRODUCT_CHECKED.getCode());
            cartMapper.insert(cart1);
        }else {
            //更新
            Cart cart1 = new Cart();
            cart1.setId(cart.getId());
            cart1.setProductId(productId);
            cart1.setUserId(userId);
            cart1.setQuantity(count);
            cart1.setChecked(cart.getChecked());
            cartMapper.updateByPrimaryKey(cart1);
        }

        CartVO cartVO = getCartVOLimit(userId);
        return ServerResponse.createServerResponseBySuccess(cartVO);
    }

    @Override
    public ServerResponse list(Integer userId) {

        CartVO cartVO = getCartVOLimit(userId);
        return ServerResponse.createServerResponseBySuccess(cartVO);
    }

    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
        //参数判断
        if(productId==null || count == null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        //查询购物车中商品
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if(cart!=null){
            //更新数量
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
        }

        //返回cartvo
        return ServerResponse.createServerResponseBySuccess(userId);
    }

    @Override
    public ServerResponse delete_product(Integer userId, String productIds) {
        //参数非空校验
        if(productIds==null || productIds.equals("")){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }
        //productIds --->List<Integer>
        List<Integer> productIdList=Lists.newArrayList();
        String[] productIdsArr = productIds.split(",");
        if(productIdsArr!=null&&productIdsArr.length>0){
            for(String productIdstr:productIdsArr){
                Integer productId = Integer.parseInt(productIdstr);
                productIdList.add(productId);
            }
        }
        //调用dao
        cartMapper.deleteByUserIdAndProductIds(userId,productIdList);
        //返回结果
        return ServerResponse.createServerResponseBySuccess(getCartVOLimit(userId));
    }

    @Override
    public ServerResponse select(Integer userId, Integer productId,Integer check) {
        //非空校验
//        if(productId==null){
//            return ServerResponse.createServerResponseByError("参数不能为空");
//        }
        //dao接口
        cartMapper.selectOrUnselectProduct(userId,productId,check);
        //返回结果


        return ServerResponse.createServerResponseBySuccess(getCartVOLimit(userId));
    }

    @Override
    public ServerResponse get_cart_product_count(Integer userId) {

        int quantity = cartMapper.get_cart_product_count(userId);
        return ServerResponse.createServerResponseBySuccess(quantity);
    }

    private CartVO getCartVOLimit(Integer userId){
        CartVO cartVO = new CartVO();
        //根据userId查询购物信息 --->List<Cart>
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);

        //List<Cart>   --->List<CartProductVO>
        List<CartProductVO> cartProductVOList = Lists.newArrayList();
        //购物车总价格
        BigDecimal carttoalprice=new BigDecimal("0");
        if(cartList!=null && cartList.size()>0){
            for(Cart cart:cartList){
                CartProductVO cartProductVO = new CartProductVO();
                cartProductVO.setId(cart.getId());
                cartProductVO.setQuantity(cart.getQuantity());
                cartProductVO.setUserId(userId);
                cartProductVO.setProductChecked(cart.getChecked());
                //查询商品
                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(product!=null){
                    cartProductVO.setProductId(cart.getProductId());
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductStock(product.getStock());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    int stock = product.getStock();
                    int limitProductCount=0;
                    if(stock>cart.getQuantity()){
                        limitProductCount=cart.getQuantity();
                        cartProductVO.setLimitQuantity("LIMIT_NUM_SUCCESS");
                    }else {//商品库存不足
                        limitProductCount=stock;
                        //更新购物车中商品的数量
                        Cart cart1 = new Cart();
                        cart1.setId(cart.getId());
                        cart1.setQuantity(stock);
                        cart1.setProductId(cart.getProductId());
                        cart1.setChecked(cart.getChecked());
                        cart1.setUserId(userId);
                        cartMapper.updateByPrimaryKey(cart1);

                        cartProductVO.setLimitQuantity("LIMIT_NUM_FAIL");
                    }
                    cartProductVO.setQuantity(limitProductCount);
                    cartProductVO.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),Double.valueOf(cartProductVO.getQuantity())));


                }

                if(cartProductVO.getProductChecked()==Const.CartCheckedEeum.PRODUCT_CHECKED.getCode()){//被选中计算总价
                    carttoalprice=BigDecimalUtils.add(carttoalprice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }

               cartProductVOList.add(cartProductVO);
            }
        }
        cartVO.setCartProductVOList(cartProductVOList);
        //计算总价格
        cartVO.setCarttotalprice(carttoalprice);
        //判断购物车是否全选
        int count = cartMapper.isCheckedAll(userId);
        if (count>0){
            cartVO.setIsallchecked(false);
        }else {
            cartVO.setIsallchecked(true);
        }
        //返回结果
        return cartVO;
    }
}
