package com.miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobject.UserVo;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author aixscode.github.io
 * @date 2019/1/2 15:48
 */
@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*" )
public class UserController  extends  BaseController{

    @Autowired
    private UserService  userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户登录接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public  CommonReturnType login(@RequestParam(name = "telephone")String telephone,
                                   @RequestParam(name = "password")String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telephone)||StringUtils.isEmpty(password))
        {
            throw  new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户校验用来校验用户登录是否合法

        UserModel userModel = userService.validateLogin(telephone,this.EnCodeByMd5(password));

        //将用户凭证加入到登录成功的session内

        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);
    }
    //用户注册接口
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telephone")String telephone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password
                                     ) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //验证手机号和对应的otpCode相符合

        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telephone);

        if(! StringUtils.equals(otpCode,inSessionOtpCode))
        {
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");

        }

        //用户注册流程

        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelephone(telephone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EnCodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);

    }
    public  String EnCodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5= MessageDigest.getInstance("MD5");
        BASE64Encoder base64en= new BASE64Encoder();

        //加密字符串
        String newstr= base64en.encode(md5.digest(str.getBytes("utf-8")));

        return  newstr;
    }

    //用户获取otp短信接口
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOpt(@RequestParam(name="telephone")String telephone)
    {
        //需按照一定的规则生成opt验证码

        Random random = new Random();
        int randomInt= random.nextInt(99999);
        randomInt+=100000;
        String optCode= String.valueOf(randomInt);

        //将OPT验证码同对应的用户的手机号关联，使用httpsession的方式绑定手机号和optCode

        httpServletRequest.getSession().setAttribute(telephone,optCode);

        //将opt验证码   通过短信发送给用户
        System.out.println("telephone ="+ telephone + "& optCode" + optCode);

        return CommonReturnType.create(null);

    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BussinessException {

        //调用service 服务端获取对应的id的用户对象并返回给前端
        UserModel userModel =userService.getUserById(id);

        if(userModel==null)
        {
            throw  new BussinessException(EmBusinessError.USER_NOT_EXIST);
        }

        //将核心领域模型用户对象转化为可供UI使用的viewobject
        UserVo userVo=convertFromModel(userModel);

        //返回用户通用对象
        return CommonReturnType.create(userVo);

    }
    private UserVo convertFromModel(UserModel userModel)
    {
        if(userModel == null)
        {
            return  null;
        }
        UserVo userVO= new UserVo();

        BeanUtils.copyProperties(userModel,userVO);

        return  userVO;
    }



}
