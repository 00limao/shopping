package com.jx.Controller.backend;

import com.jx.Service.UserService;
import com.jx.common.Const;
import com.jx.common.ServerResponse;
import com.jx.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 后台用户控制器类
 * */
@RestController
@RequestMapping(value = "/manage/user")
public class UserManageController {

    @Autowired
    UserService userService;
    //管理员登录
    @RequestMapping(value = "/login.do")
    public ServerResponse login(HttpSession session, String username, String password){

        ServerResponse serverResponse = userService.login(username, password);
        if(serverResponse.isSuccess()){
            UserInfo userInfo = (UserInfo) serverResponse.getDate();
            session.setAttribute(Const.CURRENTUSER,userInfo);
        }
        return serverResponse;
    }
}
