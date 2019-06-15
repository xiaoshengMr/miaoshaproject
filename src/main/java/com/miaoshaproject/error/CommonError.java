package com.miaoshaproject.error;

/**
 * @author aixscode.github.io
 * @date 2019/1/3 19:42
 */
public interface CommonError {
    public int getErrorCode();
    public String getErrorMsg();
    public  CommonError setErrorMsg(String errorMsg);
}
