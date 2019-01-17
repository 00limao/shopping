package com.jx.Service;

import com.jx.common.ServerResponse;
import com.jx.pojo.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface IProductService {

    //添加和更新用户
    ServerResponse saveUpdate(Product product);

    //商品上下架
    ServerResponse set_sale_status(Integer productId,Integer status);

    //后台 - 商品详情
    ServerResponse detail(Integer productId);

    //后台-商品列表-分页
    ServerResponse list(Integer pageNum,Integer pageSize);

    //后台 - 商品搜索
    ServerResponse search(Integer productId,String productName,Integer pageNum,Integer pageSize);

    //图片上传
    ServerResponse upload(MultipartFile file,String path);

    //前台 - 商品详情
    ServerResponse detail_protal(Integer productId);

    //前台 - 商品搜索
    ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy);
}
