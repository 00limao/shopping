package com.jx.Service.impl;

import com.google.common.collect.Sets;
import com.jx.Service.ICategoryService;
import com.jx.common.ServerResponse;
import com.jx.dao.CategoryMapper;
import com.jx.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiecImpl implements ICategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServerResponse get_category(Integer categoryId) {

        //1,非空校验
        if(categoryId==null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }

        //2.根据category查询类别
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category==null){
            return ServerResponse.createServerResponseByError("查询的类别不存在");
        }
        //3，查询子类别
        List<Category> categoryList = categoryMapper.findChildCategory(categoryId);
        //4,返回结果
        return ServerResponse.createServerResponseBySuccess(categoryList);
    }

    @Override
    public ServerResponse add_category(Integer parentId, String categoryName) {
        //1,参数校验
        if(categoryName==null || categoryName.equals("")){
            return ServerResponse.createServerResponseByError("类别不能为空");
        }
        //2,添加节点
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(1);
        int result = categoryMapper.insert(category);
        //3,返回结果
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError("添加失败");
    }

    @Override
    public ServerResponse set_category_name(Integer categoryId, String categoryName) {
        //1,参数非空校验
        if(categoryId==null || categoryId.equals("")){
            return ServerResponse.createServerResponseByError("类别id不能为空");
        }
        if(categoryName==null || categoryName.equals("")){
            return ServerResponse.createServerResponseByError("类别名不能为空");
        }

        //2,根据categoryId查询
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category==null){
            return ServerResponse.createServerResponseByError("要修改的类别不存在");
        }
        //3，修改
        category.setName(categoryName);
        int result = categoryMapper.updateByPrimaryKey(category);
        //4,返回结果
        if(result>0) {
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError("修改失败");
    }

    @Override
    public ServerResponse get_deep_category(Integer categoryId) {
        //1,非空校验
        if(categoryId==null){
            return ServerResponse.createServerResponseByError("类别id不能为空");
        }
        //2,查询
        Set<Category> categorySet = Sets.newHashSet();
        categorySet = findAllChildCategory(categorySet,categoryId);
        Set<Integer> integerSet = Sets.newHashSet();
        Iterator<Category> categoryIterator = categorySet.iterator();
        while(categoryIterator.hasNext()){
            Category category = categoryIterator.next();
            integerSet.add(category.getId());
        }

        return ServerResponse.createServerResponseBySuccess(integerSet);
    }

    private Set<Category> findAllChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }
        //查找categoryId下的子节点（平级）
        List<Category> categoryList = categoryMapper.findChildCategory(categoryId);
        if(categoryList!=null&&categoryList.size()>0){
            for(Category category1:categoryList){
                findAllChildCategory(categorySet,category1.getId());
            }
        }
        return categorySet;
    }
}
