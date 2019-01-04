package com.jx.Controller;

import com.jx.dao.UserInfoMapper;
import com.jx.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    UserInfoMapper userInfoMapper;

    @RequestMapping("/user/{id}")
    public UserInfo findUser(@PathVariable Integer id){
        return userInfoMapper.selectByPrimaryKey(id);
    }


}
