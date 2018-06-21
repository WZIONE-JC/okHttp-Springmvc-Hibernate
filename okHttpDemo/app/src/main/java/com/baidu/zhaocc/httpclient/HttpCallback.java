package com.baidu.zhaocc.httpclient;

/**
 * <p>
 * Created by zhaochaochao@baidu.com on 2018/6/13.
 */
public interface HttpCallback<T> {
    /**
     * 响应成功
     */
    void onReqSuccess(T result);
    /**
     * 响应失败
     */
    void onReqFail(String errorMsg);
}
