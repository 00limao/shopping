package com.jx.Service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jx.Service.ICategoryService;
import com.jx.Service.IProductService;
import com.jx.common.Const;
import com.jx.common.ServerResponse;
import com.jx.dao.CategoryMapper;
import com.jx.dao.ProductMapper;
import com.jx.pojo.Category;
import com.jx.pojo.Product;
import com.jx.utils.DateUtils;
import com.jx.utils.FTPUtil;
import com.jx.utils.PropertiesUtils;
import com.jx.vo.ProductDetailVO;
import com.jx.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ICategoryService categoryService;
    @Override
    public ServerResponse saveUpdate(Product product) {
        //1,参数非空校验
        if(product==null){
            return ServerResponse.createServerResponseByError("参数为空");
        }
        //2,设置商品的主图 sub_images  ---->1.jpg,2.jpg,3.jpg
        String subImages = product.getSubImages();
        if(subImages!=null&&!subImages.equals("")){
            String[] subImageArr = subImages.split(",");
            if(subImageArr.length>0){
                product.setMainImage(subImageArr[0]);
            }
        }
        //3,商品 save or update
        if(product.getId()==null){
            //添加
            int result = productMapper.insert(product);
            if(result>0){
                return ServerResponse.createServerResponseBySuccess();
            }else {
                return ServerResponse.createServerResponseByError("添加失败");
            }
        }else {
            //更新
            int result = productMapper.updateByPrimaryKey(product);
            if (result>0){
                return ServerResponse.createServerResponseBySuccess();
            }else{
                return ServerResponse.createServerResponseByError("更新失败");
            }

        }
    }

    @Override
    public ServerResponse set_sale_status(Integer productId, Integer status) {

        //1,参数非空校验
        if(productId==null){
            return ServerResponse.createServerResponseByError("商品ID参数不能为空");
        }
        if(status==null){
            return ServerResponse.createServerResponseByError("商品状态参数不能为空");
        }
        //2,更新商品状态
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int result = productMapper.updateProductKeySelective(product);
        //3,返回结果
        if (result>0){
            return ServerResponse.createServerResponseBySuccess();
        }else{
            return ServerResponse.createServerResponseByError("更新失败");
        }


    }

    @Override
    public ServerResponse detail(Integer productId) {
        //1,非空校验
        if(productId==null){
            return ServerResponse.createServerResponseByError("商品ID不能为空");
        }
        //2,查询product
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createServerResponseByError("商品不存在");
        }
        //3,product----->productDetailVO
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
        //4,返回结果
        return ServerResponse.createServerResponseBySuccess(productDetailVO);
    }



    private ProductDetailVO assembleProductDetailVO(Product product){



        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setCreateTime(DateUtils.dateToStr(product.getCreateTime()));
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
        productDetailVO.setName(product.getName());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setId(product.getId());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setUpdateTime(DateUtils.dateToStr(product.getUpdateTime()));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category!=null)
        {
            productDetailVO.setParentCategoryId(category.getParentId());
        }else {
            productDetailVO.setParentCategoryId(0);
        }
        return productDetailVO;
    }
    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum,pageSize);
        //1,查询商品数据
        List<Product> products = productMapper.selectAll();
        PageInfo pageInfo = new PageInfo(products);
        return ServerResponse.createServerResponseBySuccess(null,pageInfo);
    }

    @Override
    public ServerResponse search(Integer productId, String productName, Integer pageNum, Integer pageSize) {
        //select * from product where productId ? and productName like %name%
        PageHelper.startPage(pageNum,pageSize);

        if (productName!=null&&!productName.equals("")){
            productName="%"+productName+"%";
        }
        List<Product> productList = productMapper.findProductByProductIdAndProductName(productId, productName);
        PageInfo pageInfo = new PageInfo(productList);
        return ServerResponse.createServerResponseBySuccess(null,pageInfo);
    }

    @Override
    public ServerResponse upload(MultipartFile file, String path) {

        if(file==null){
            return ServerResponse.createServerResponseByError();
        }

        //1,读取图片名称
        String originalFilename = file.getOriginalFilename();
        //获取图片扩展名
        String enName = originalFilename.substring(originalFilename.lastIndexOf("."));
        //为图片生成新的唯一的名字
        String newFileName = UUID.randomUUID().toString()+enName;

        File pathFile = new File(path);
        if(!pathFile.exists()){
            pathFile.setWritable(true);
            pathFile.mkdirs();
        }
        File file1 = new File(path,newFileName);
        try {
            file.transferTo(file1);
            //上传到图片服务器
            FTPUtil.uploadFile(Lists.<File>newArrayList(file1));
            Map<String,String> map = Maps.newHashMap();
            map.put("uri",newFileName);
            map.put("url",PropertiesUtils.readByKey("imageHost")+"/"+newFileName);

            //删除应用服务器上的图片
            file1.delete();
            return ServerResponse.createServerResponseBySuccess(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //前台 商品详细
    @Override
    public ServerResponse detail_protal(Integer productId) {

        //1,参数校验
        if(productId==null){
            return ServerResponse.createServerResponseByError("商品ID不能为空");
        }
        //2,查询product
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createServerResponseByError("商品不存在");
        }
        //3,校验商品状态
        if(product.getStatus()!= Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
            return ServerResponse.createServerResponseByError("商品已下架或删除");
        }

        //4,获取productDetailVO
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);

        //5,返回结果
        return ServerResponse.createServerResponseBySuccess(productDetailVO);
    }

    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());
        return productListVO;
    }
    @Override
    public ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
        // 1,参数校验  categoryId和keyword不能同时为空
        if(categoryId == null && (keyword == null || keyword.equals(""))){
            return ServerResponse.createServerResponseByError("参数错误");
        }
        //2,categoryId
        Set<Integer> integerSet = Sets.newHashSet();
        if(categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null&&(keyword==null ||keyword.equals(""))) {
                //说明没有商品数据
                PageHelper.startPage(pageNum,pageSize);
                ArrayList<ProductDetailVO> productDetailVOList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productDetailVOList);
                return ServerResponse.createServerResponseBySuccess(pageInfo);
            }

            ServerResponse serverResponse = categoryService.get_deep_category(categoryId);

            if (serverResponse.isSuccess()){
                integerSet =(Set<Integer>) serverResponse.getDate();
            }
        }
        //3,keyword
            if(keyword!=null&&!keyword.equals("")){
                keyword="%" + keyword + "%";
            }else{
                keyword=null;
            }

            if(orderBy.equals("")){
                PageHelper.startPage(pageNum,pageSize);
            }else{
                String[] orderByArr = orderBy.split("_");
                if(orderByArr.length==2){
                    PageHelper.startPage(pageNum,pageSize,orderByArr[0]+" "+orderByArr[1]);
                }else if (orderByArr.length==3){
                    PageHelper.startPage(pageNum,pageSize,orderByArr[0]+"_"+orderByArr[1]+ " " +orderByArr[2]);
                }else{
                    PageHelper.startPage(pageNum,pageSize);
                }
            }
        //4,List<Product>  --->List<ProductListVO>
            List<Product> productList = productMapper.searchProduct(integerSet, keyword);
            List<ProductListVO> productListVOList =Lists.newArrayList();
            if(productList!=null&&productList.size()>0){
                for(Product product:productList){
                    ProductListVO productListVO = assembleProductListVO(product);
                    productListVOList.add(productListVO);
                }
            }
        //5,分页
            PageInfo pageInfo = new PageInfo();
            pageInfo.setList(productListVOList);
        //6,返回
        return ServerResponse.createServerResponseBySuccess(pageInfo);
    }
}
