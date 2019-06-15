package com.miaoshaproject.service;

import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.service.model.UserModel;

/**
 * @author aixscode.github.io
 * @date 2019/1/2 15:52
 */
public interface UserService {

        //通过用户id获取对象id的方法
        UserModel getUserById(Integer id);

        //用户注册
        void register(UserModel userModel) throws BussinessException;

        //用户登录

        /**
         *
         * @param telephone 用户注册手机
         * @param encrptPassword  用户加密后的密码
         * @throws BussinessException
         */
        UserModel validateLogin(String telephone,String encrptPassword) throws BussinessException;
}
