package com.jx.Controller;

import com.jx.common.ServerResponse;
import com.jx.dao.UserInfoMapper;
import com.jx.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

//    @Autowired
//    UserInfoMapper userInfoMapper;
//
//    @RequestMapping("/user/{id}")
//    public ServerResponse<UserInfo> findUser(@PathVariable Integer id){
//        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(id);
//
//        if (userInfo!=null){
//            return ServerResponse.createServerResponseBySuccess(null,userInfo);
//        }else {
//            return ServerResponse.createServerResponseByError("失败");
//        }
//    }
}
