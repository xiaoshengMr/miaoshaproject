package com.miaoshaproject.service.impl;

import com.alibaba.druid.sql.visitor.functions.If;
import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dao.UserPasswordDOMapper;
import com.miaoshaproject.dataobject.UserDO;
import com.miaoshaproject.dataobject.UserPasswordDO;
import com.miaoshaproject.error.BussinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.validation.Validator;

/**
 * @author aixscode.github.io
 * @date 2019/1/2 15:53
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired

    private ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        //调用userdomapper获取对应用户dataobject
       UserDO userDO = userDOMapper.selectByPrimaryKey(id);
       if(userDO==null)
       {
           return  null;
       }
       //通过id获取用户加密密码信息
       UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());

       return  convertFromDataObject(userDO,userPasswordDO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BussinessException {
        if (userModel==null)
        {
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        /*
        if(StringUtils.isEmpty(userModel.getName())||userModel.getGender()==null
                ||userModel.getAge()==null
                ||StringUtils.isEmpty(userModel.getTelephone())
                )
        {
            throw  new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        */
       ValidationResult result= validator.validate(userModel);

       if(result.isHasError())
       {
           throw  new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
       }

       //实现model->databject
        UserDO userDO =convertFromModel(userModel);

        try {
            userDOMapper.insertSelective(userDO);
        }
        catch (DuplicateKeyException ex)
        {
            throw new  BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已重复注册");
        }

        userModel.setId(userDO.getId());
        UserPasswordDO userPasswordDO =convertPasswordFromModel(userModel);

        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }

    @Override
    public UserModel validateLogin(String telephone, String encrptPassword) throws BussinessException {

        //通过用户手机号获取用户信息

        UserDO userDO= userDOMapper.selectByTelephone(telephone);
        if(userDO==null)
        {
            throw  new BussinessException(EmBusinessError.USER_lOGIN_FAIL);
        }

        UserPasswordDO userPasswordDO=userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel =convertFromDataObject(userDO,userPasswordDO);

        //比对用户信息内加密的密码和传输进来的密码相匹配

        if (!StringUtils.equals(encrptPassword,userModel.getEncrptPassword()))
        {
            throw  new BussinessException(EmBusinessError.USER_lOGIN_FAIL);
        }

        return userModel;
    }
    private  UserPasswordDO convertPasswordFromModel(UserModel userModel)
    {
        if(userModel==null)
        {
            return  null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();

        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());

        userPasswordDO.setUserId(userModel.getId());

        return  userPasswordDO;

    }
    private UserDO convertFromModel(UserModel userModel)
    {
        if (userModel==null)
        {
            return  null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return  userDO;
    }

    private  UserModel  convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO)
    {
        if(userDO==null) {return  null;}
        UserModel userModel= new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        if(userPasswordDO!=null)
        {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return  userModel;
    }
}
