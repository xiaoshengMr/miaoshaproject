package com.miaoshaproject.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;


/**
 * @author aixscode.github.io
 * @date 2019/1/8 13:43
 */
@Component
public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    //实现校验方法并返回校验结果

    public  ValidationResult validate(Object bean)
    {

        final  ValidationResult result = new ValidationResult();

        Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(bean);

        if(constraintViolationSet.size()>0)
        {
            //有错误
            result.setHasError(true);
            constraintViolationSet.forEach(constrainViolation->{
                String errMsg = constrainViolation.getMessage();
                String propertyName=constrainViolation.getPropertyPath().toString();
                result.getErrorMsgMap().put(propertyName,errMsg);
            });
        }
        return  result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //通过hibernate vaildator通过工厂的初始化方式使其实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
