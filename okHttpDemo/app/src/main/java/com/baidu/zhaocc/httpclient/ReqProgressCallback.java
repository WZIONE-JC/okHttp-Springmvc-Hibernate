package com.baidu.zhaocc.httpclient;

/**
 * <p>
 * Created by zhaochaochao@baidu.com on 2018/6/20.
 */
public interface ReqProgressCallback<T> extends HttpCallback<T> {
    /**
     * 响应进度更新
     */
    void onProgress(long total, long current);
}
