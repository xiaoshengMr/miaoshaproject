package com.miaoshaproject.error;

/**
 * @author aixscode.github.io
 * @date 2019/1/3 19:53
 */

//包装器业务异常类实现
public class BussinessException extends  Exception implements  CommonError {


    private  CommonError commonError;

    //直接接受EmBussinessError的传参用于构造业务异常
    public  BussinessException(CommonError commonError)
    {
        super();
        this.commonError=commonError;
    }

    //接受自定义errMsg的方式构造业务异常
    public  BussinessException(CommonError commonError,String errMsg)
    {
        super();
        this.commonError  =commonError;
        this.commonError.setErrorMsg(errMsg);
    }
    @Override
    public int getErrorCode() {
        return this.commonError.getErrorCode();
    }

    @Override
    public String getErrorMsg() {
        return this.commonError.getErrorMsg();
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.commonError.setErrorMsg(errorMsg);
        return this;
    }

}
