package com.jx.Service.impl;

import com.jx.Service.UserService;
import com.jx.common.Const;
import com.jx.common.ServerResponse;
import com.jx.dao.UserInfoMapper;
import com.jx.pojo.UserInfo;
import com.jx.utils.MD5Utils;
import com.jx.utils.TokenCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public ServerResponse login(String username, String password) {

        //1，参数的非空校验
        if (username==null || username.equals(""))
        {
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        if(password==null || password.equals("")){
            return ServerResponse.createServerResponseByError("密码不能为空");
        }

        //2，检查用户名是否存在
        int result = userInfoMapper.checkUsername(username);
        if(result==0){
            return ServerResponse.createServerResponseByError("用户名不存在");
        }

        //3，根据用户名和密码查找用户信息
        UserInfo userInfo = userInfoMapper.selectUserInfoByUsernameAndPassword(username,MD5Utils.getMD5Code(password));
        if(userInfo==null){
            return ServerResponse.createServerResponseByError("密码错误");
        }
        //4，返回结果
       // userInfo.setPassword("");
        return ServerResponse.createServerResponseBySuccess(userInfo);
    }

    @Override
    public ServerResponse register(UserInfo userInfo) {
        //1,参数非空校验
        if(userInfo==null){
            return ServerResponse.createServerResponseByError("参数必须");
        }
        //2,校验用户名
        int result = userInfoMapper.checkUsername(userInfo.getUsername());
        if(result>0){
            return ServerResponse.createServerResponseByError("用户名已存在");
        }
        //3,校验邮箱
        int result_email = userInfoMapper.checkEmail(userInfo.getEmail());
        if(result_email>0){
            return ServerResponse.createServerResponseByError("邮箱已存在");
        }
        //4,注册
        userInfo.setRole(Const.RoleEeum.ROLE_CUSTOMER.getCode());
        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));
        int count = userInfoMapper.insert(userInfo);
        if(count>0) {
            return ServerResponse.createServerResponseBySuccess("注册成功");
        }
        //5,返回结果
            return ServerResponse.createServerResponseByError("注册失败");
    }

    @Override
    public ServerResponse forget_get_question(String username) {
        //1,参数校验
        if(username==null || username.equals("")){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        //2,校验username
        int result = userInfoMapper.checkUsername(username);
        if(result==0){
            return ServerResponse.createServerResponseByError("用户名不存在，请重新输入");
        }

        //3,查找密保问题
        String question = userInfoMapper.selectQuestionByUsername(username);
        if(question==null || question.equals("")){
            return  ServerResponse.createServerResponseByError("密保问题空");
        }
        return ServerResponse.createServerResponseBySuccess(question);
    }

    @Override
    public ServerResponse forget_get_answer(String username, String question, String answer) {
        //1,参数校验
        if(username==null || username.equals("")){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        if(question==null || question.equals("")){
            return ServerResponse.createServerResponseByError("问题不能为空");
        }
        if(answer==null || answer.equals("")){
            return ServerResponse.createServerResponseByError("答案不能为空");
        }

        //2,根据username,question,answer查询
        int result = userInfoMapper.selectByUsernameAndQuestionAndAswer(username, question, answer);
        if(result==0){
            return ServerResponse.createServerResponseByError("答案错误");
        }
        //3,服务端生成一个token保存并将token返回给客户端
        String forgetToken = UUID.randomUUID().toString();
        //guava cache 缓存
        TokenCache.set(username,forgetToken);
        return ServerResponse.createServerResponseBySuccess(forgetToken);

    }

    @Override
    public ServerResponse forget_reset_password(String username, String password, String forgetToken) {

        //1,参数校验
        if(username==null || username.equals("")){
            return ServerResponse.createServerResponseByError("用户名不能为空");
        }
        if(password==null || password.equals("")){
            return ServerResponse.createServerResponseByError("密码不能为空");
        }
        if(forgetToken==null || forgetToken.equals("")){
            return ServerResponse.createServerResponseByError("token不能为空");
        }
        //2,token检验
        String token = TokenCache.get(username);
        if(token==null){
            return ServerResponse.createServerResponseByError("token过期");
        }
        if(!token.equals(forgetToken)){
            return ServerResponse.createServerResponseByError("无效的token");
        }

        //3,修改密码
        int result = userInfoMapper.updateUserPassword(username, MD5Utils.getMD5Code(password));
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError("修改密码失败");

    }

    @Override
    public ServerResponse check_valid(String str, String type) {
        //1,参数校验
        if(str==null || str.equals("")){
            return ServerResponse.createServerResponseByError("用户名或者邮箱不能为空");
        }
        if(type==null || type.equals("")){
            return ServerResponse.createServerResponseByError("检验的类型参数不能为空");
        }

        // type:username  检验用户名str
        //     :email    校验邮箱str
        if(type.equals("username")){
            int result = userInfoMapper.checkUsername(str);
            if(result>0){
                return ServerResponse.createServerResponseByError("用户名已存在");
            }else{
                return ServerResponse.createServerResponseBySuccess();
            }
        }else if(type.equals("email")){
            int result = userInfoMapper.checkEmail(str);
            if(result>0){
                return ServerResponse.createServerResponseByError("邮箱已存在");
            }else{
                return ServerResponse.createServerResponseBySuccess();
            }
        }else{
            return ServerResponse.createServerResponseByError("参数类型错误");
        }
    }

    @Override
    public ServerResponse reset_password(String username,String passwordOld, String passwordNew) {
        //1,参数校验
        if(passwordOld==null || passwordOld.equals("")){
            return ServerResponse.createServerResponseByError("用户旧密码不能为空");
        }
        if(passwordNew==null || passwordNew.equals("")){
            return ServerResponse.createServerResponseByError("用户新密码不能为空");
        }
        //2,根据username和Oldpassword
        UserInfo userInfo = userInfoMapper.selectUserInfoByUsernameAndPassword(username,MD5Utils.getMD5Code(passwordOld));
        if(userInfo==null){
            return ServerResponse.createServerResponseByError("旧密码错误");
        }

        //3,修改密码
        userInfo.setPassword(MD5Utils.getMD5Code(passwordNew));
        int result = userInfoMapper.updateByPrimaryKey(userInfo);
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError("密码修改失败");
    }

    @Override
    public ServerResponse update_information(UserInfo user) {

        //1,参数校验
        if(user==null){
            return ServerResponse.createServerResponseByError("参数不能为空");
        }

        //2,更新用户信息
        int result = userInfoMapper.updateUserBySelectActive(user);
        if(result>0){
            return ServerResponse.createServerResponseBySuccess();
        }
        return ServerResponse.createServerResponseByError("更新个人信息失败");

    }

    @Override
    public UserInfo findUserInfoByUserid(Integer userId) {

        return userInfoMapper.selectByPrimaryKey(userId);
    }


}
