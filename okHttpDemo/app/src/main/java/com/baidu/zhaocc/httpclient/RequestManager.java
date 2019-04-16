package com.baidu.zhaocc.httpclient;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.baidu.zhaocc.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * <p>
 * Created by zhaocc@baidu.com on 2018/6/13.
 */
public class RequestManager {
    private static final String TAG = RequestManager.class.getSimpleName();
    private static RequestManager sRequestManager;
    private Handler mHandler;
    private static final String ZHAOCC_CRT =
            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIDejCCAmKgAwIBAgIEOzIKajANBgkqhkiG9w0BAQsFADBiMQswCQYDVQQGEwJj\n" +
                    "bjEQMA4GA1UECBMHQmVpamluZzEQMA4GA1UEBxMHQmVpamluZzEOMAwGA1UEChMF\n" +
                    "YmFpZHUxDjAMBgNVBAsTBWJhaWR1MQ8wDQYDVQQDEwZ6aGFvY2MwHhcNMTkwMzIy\n" +
                    "MDMxOTQ3WhcNMTkwNjIwMDMxOTQ3WjBiMQswCQYDVQQGEwJjbjEQMA4GA1UECBMH\n" +
                    "QmVpamluZzEQMA4GA1UEBxMHQmVpamluZzEOMAwGA1UEChMFYmFpZHUxDjAMBgNV\n" +
                    "BAsTBWJhaWR1MQ8wDQYDVQQDEwZ6aGFvY2MwggEiMA0GCSqGSIb3DQEBAQUAA4IB\n" +
                    "DwAwggEKAoIBAQCnxVhH/2rnazBq7euECUj+1h8Fu3OmZ9ukZuGHTZU9ih8vq6yk\n" +
                    "xFrD2MFmt9xzcUlvVDqcOJnx1J/demwvadmRMjKYbFDOEtYsO3E6XvZt2Cj+NJ+O\n" +
                    "toGTfEcu80MJHG6TanXwvVTqPLH0Lzo2l6x4jmhRC8GCSIjlRfA3cRvhcyWGeR85\n" +
                    "4+3YM8+emDRffrWiP86ip0szjcHKpkmti/JYD8ydHIalNCEPOktdbfrcOdzgqBnA\n" +
                    "EQOV4Ec8aj6WvcxotwV7owvWtDpPadkuq7suI7OYUNuHgg1GvF0Wn5ldYLmwh8HS\n" +
                    "MMnWKBPApLLruCkv3JISD/iZwEzp+jf+UWZvAgMBAAGjODA2MBUGA1UdEQQOMAyC\n" +
                    "BHNwYW2HBMCoAAEwHQYDVR0OBBYEFBN4E7VADxgsflIeP0ukacKdYdAHMA0GCSqG\n" +
                    "SIb3DQEBCwUAA4IBAQBFMfigOxccCmNjz8vy0SCmaptLIBkLiMLSrB7FNPi7YPEp\n" +
                    "m16cg+23GHQCoNCNMPG5Bt4SsvFA7ts2lD6X729hll1W+om5g/3nQIR3rfFoW9it\n" +
                    "iIuNs6R6kThK76R1KVSG/fs+VXswRkx2E1jhw4IOYiuJfhHbW2+Mi1Ypn7tLVnSk\n" +
                    "Dwyu7Axo3tcoy+ksX72G7ODbEIZQQCTiyp7MD2iu5ceS4FQJPfUfq/wu7EyzLlV9\n" +
                    "z+pCt/NtmhwxrS6dK9Mx+9+S3/c9gwSv5sJ7QiYyY1qYBOCul13iy3lV4nh2hZym\n" +
                    "otgqyc01nXDaHAUVPucoTPcMH1Zdb58axgMgNRHV\n" +
                    "-----END CERTIFICATE-----";

    private static final MediaType MEDIA_TYPE_FORM = MediaType.parse("application/x-www-form-urlencoded; " +
            "charset=utf-8");
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");

    // 请求接口根地址
    private static final String HTTP_BASE_URL = "http://172.21.205.137:8080";
    private static final String HTTPS_BASE_URL = "https://172.21.205.137:443";
    public static final int TYPE_GET = 0;//get请求
    public static final int TYPE_POST_JSON = 1;//post请求参数为json
    public static final int TYPE_POST_FORM = 2;//post请求参数为表单
    private OkHttpClient mOkHttpClient;//okHttpClient 实例

    public static synchronized RequestManager getInstance(Context context) {
        if (sRequestManager == null) {
            sRequestManager = new RequestManager(context);
        }
        return sRequestManager;
    }

    public RequestManager(Context context) {
        //初始化OkHttpClient
        try {
            final X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    LOGD("checkClientTrusted chain: " + chain + " authType: " + authType);
                    for (X509Certificate certificate : chain) {
                        LOGD("certificate: " + certificate);
                    }
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    LOGD("checkServerTrusted chain: " + chain + " authType: " + authType);
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    LOGD("getAcceptedIssuers");
                    return new X509Certificate[0];
                }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            mOkHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)           //设置超时时间
                    .readTimeout(10, TimeUnit.SECONDS)              //设置读取超时时间
                    .writeTimeout(10, TimeUnit.SECONDS)             //设置写入超时时间
//                    .sslSocketFactory(setCertificates(context.getAssets().open("zhaocc01.crt")))
                    .sslSocketFactory(setCertificates(context, new Buffer().writeUtf8(ZHAOCC_CRT).inputStream()))
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            try {
                                LOGD("HostnameVerifier hostname: " + hostname + " session: " + session);
                                session.getPeerCertificateChain();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    })
                    .cookieJar(new CookieJar() {
                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            Log.d(TAG, "saveFromResponse url: " + url + " cookies: " + cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            Log.d(TAG, "loadForRequest url: " + url);
                            Cookie cookie = new Cookie.Builder()
                                    .hostOnlyDomain(url.host())
                                    .name("token")
                                    .value("this is token")
                                    .build();
                            List<Cookie> cookies = new ArrayList<>();
                            cookies.add(cookie);
                            return cookies;
                        }
                    })
                    .build();
            mHandler = new Handler(context.getMainLooper());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步请求
     *
     * @param actionUrl   the action url
     * @param requestType the request type
     * @param params      the params
     * @return the response
     */
    public String requestSync(String actionUrl, int requestType, HashMap<String, String> params) {
        switch (requestType) {
            case TYPE_GET:
                return requestGetSync(actionUrl, params);
            case TYPE_POST_FORM:
                return requestPostSyncWithForm(actionUrl, params);
            case TYPE_POST_JSON:
                return requestPostSync(actionUrl, params);
        }
        return null;
    }

    /**
     * https get请求
     *
     * @param actionUrl the action url
     */
    public String httpsGetTest(String actionUrl) {
        LOGD("httpsGetTest " + actionUrl);
        try {
            LOGD("requestUrl:" + actionUrl);
            Request request = addHeaders().url(actionUrl).build();
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();
            String body = response.body().string();
            LOGD("status:" + response.code() + " response:" + body);
            LOGD("full response:" + response.toString());
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 同步get请求
     *
     * @param actionUrl the action url
     * @param params    the params
     */
    private String requestGetSync(String actionUrl, HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        try {
            int pos = 0;
            for (String key : params.keySet()) {
                if (pos > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s", key, URLEncoder.encode(params.get(key), "UTF-8")));
                pos++;
            }
            String requestUrl = String.format("%s/%s?%s", HTTPS_BASE_URL, actionUrl, sb);
            LOGD("requestUrl:" + requestUrl);
            Request request = addHeaders().url(requestUrl).build();
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();
            String body = response.body().string();
            LOGD("status:" + response.code() + "response headers:" + response.headers() + " response body:" + body);
            LOGD("full response:" + response.toString());
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 同步post请求提交json数据
     *
     * @param actionUrl the action url
     * @param params    the params
     */
    private String requestPostSync(String actionUrl, HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        try {
            int pos = 0;
            for (String key : params.keySet()) {
                if (pos > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s", key, URLEncoder.encode(params.get(key), "UTF-8")));
                pos++;
            }
            String requestUrl = String.format("%s/%s", HTTPS_BASE_URL, actionUrl);
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, sb.toString());
            LOGD("requestUrl:" + requestUrl);
            LOGD("requestBody" + sb.toString());
            Request request = addHeaders().url(requestUrl).post(requestBody).build();
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();
            String body = response.body().string();
            LOGD("status:" + response.code() + " response:" + body);
            LOGD("full response:" + response.toString());
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 同步post请求提交表单数据
     *
     * @param actionUrl the action url
     * @param params    the params
     */
    private String requestPostSyncWithForm(String actionUrl, HashMap<String, String> params) {
        try {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (String key : params.keySet()) {
                formBodyBuilder.add(key, params.get(key));
            }
            FormBody formBody = formBodyBuilder.build();
            String requestUrl = String.format("%s/%s", HTTPS_BASE_URL, actionUrl);
            LOGD("requestUrl:" + requestUrl);
            Request request = addHeaders().url(requestUrl).post(formBody).build();
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();
            String body = response.body().string();
            LOGD("status:" + response.code() + " response:" + body);
            LOGD("full response:" + response.toString());
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步请求
     *
     * @param actionUrl   the action url
     * @param requestType the request type
     * @param params      the params
     */
    public <T> void requestAsync(String actionUrl, int requestType, HashMap<String, String> params, final
    HttpCallback<T> callback) {
        switch (requestType) {
            case TYPE_GET:
                requestGetAsync(actionUrl, params, callback);
                break;
            case TYPE_POST_FORM:
                requestPostAsyncWithForm(actionUrl, params, callback);
                break;
            case TYPE_POST_JSON:
                requestPostAsync(actionUrl, params, callback);
                break;
        }
    }

    /**
     * 异步get请求
     *
     * @param actionUrl the action url
     * @param params    the params
     */
    private <T> void requestGetAsync(String actionUrl, HashMap<String, String> params, final HttpCallback<T> callback) {
        StringBuilder sb = new StringBuilder();
        try {
            int pos = 0;
            for (String key : params.keySet()) {
                if (pos > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s", key, URLEncoder.encode(params.get(key), "UTF-8")));
                pos++;
            }
            String requestUrl = String.format("%s/%s?%s", HTTPS_BASE_URL, actionUrl, sb);
            LOGD("requestUrl:" + requestUrl);
            Request request = addHeaders().url(requestUrl).build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handleError("服务器访问失败", callback);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        handleSuccess((T) res, callback);
                    } else {
                        handleError("服务器错误", callback);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 异步post请求提交json数据
     *
     * @param actionUrl the action url
     * @param params    the params
     */
    private <T> void requestPostAsync(String actionUrl, HashMap<String, String> params, final HttpCallback<T>
            callback) {
        StringBuilder sb = new StringBuilder();
        try {
            int pos = 0;
            for (String key : params.keySet()) {
                if (pos > 0) {
                    sb.append("&");
                }
                sb.append(String.format("%s=%s", key, URLEncoder.encode(params.get(key), "UTF-8")));
                pos++;
            }
            String requestUrl = String.format("%s/%s", HTTPS_BASE_URL, actionUrl);
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, sb.toString());
            LOGD("requestUrl:" + requestUrl);
            LOGD("requestBody" + sb.toString());
            Request request = addHeaders().url(requestUrl).post(requestBody).build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handleError("服务器访问失败", callback);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        handleSuccess((T) res, callback);
                    } else {
                        handleError("服务器错误", callback);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 异步post请求提交表单数据
     *
     * @param actionUrl the action url
     * @param params    the params
     */
    private <T> void requestPostAsyncWithForm(String actionUrl, HashMap<String, String> params, final HttpCallback<T>
            callback) {
        try {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (String key : params.keySet()) {
                formBodyBuilder.add(key, params.get(key));
            }
            FormBody formBody = formBodyBuilder.build();
            String requestUrl = String.format("%s/%s", HTTPS_BASE_URL, actionUrl);
            LOGD("requestUrl:" + requestUrl);
            Request request = addHeaders().url(requestUrl).post(formBody).build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handleError("服务器访问失败", callback);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        handleSuccess((T) res, callback);
                    } else {
                        handleError("服务器错误", callback);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 以二进制流的格式上传文件
     *
     * @param <T>       the type parameter
     * @param actionUrl the action url
     * @param filePath  the file path
     * @param callBack  the call back
     */
    public <T> void uploadFile(String actionUrl, String filePath, final HttpCallback<T> callBack) {
        //补全请求地址
        String requestUrl = String.format("%s/%s", HTTPS_BASE_URL, actionUrl);
        //创建File
        File file = new File(filePath);
        //创建RequestBody
        RequestBody body = RequestBody.create(MEDIA_OBJECT_STREAM, file);
        //创建Request
        final Request request = addHeaders().addHeader("FileName", "testFile").url(requestUrl).post(body).build();
        LOGD("request:" + request.toString()
                + " headers:" + request.headers().toString()
                + " body content type:" + request.body().contentType().toString());
        final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
                handleError("上传失败", callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.e(TAG, "response ----->" + string);
                    handleSuccess((T) string, callBack);
                } else {
                    handleError("上传失败", callBack);
                }
            }
        });
    }

    /**
     * 使用multipart/form-data格式上传带参数的文件
     *
     * @param <T>      the type parameter
     * @param url      the url
     * @param params   the params
     * @param callback the callback
     */
    public <T> void uploadFile(String url, HashMap<String, Object> params, final HttpCallback<T> callback) {
        try {
            // 补全请求地址
            String requestUrl = String.format("%s/%s", HTTPS_BASE_URL, url);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            // 设置类型 multipart/form-data格式
            builder.setType(MultipartBody.FORM);
            // 追加参数
            for (String key : params.keySet()) {
                Object object = params.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(MEDIA_OBJECT_STREAM, file));
                }
            }
            // 创建RequestBody
            RequestBody requestBody = builder.build();
            // 创建Request
            Request request = addHeaders().url(requestUrl).post(requestBody).build();
            LOGD("request:" + request.toString()
                    + " headers:" + request.headers().toString()
                    + " body content type:" + request.body().contentType().toString());
            final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.toString());
                    handleError("上传失败", callback);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LOGD("uploadFile response:" + response.toString());
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.e(TAG, "response ----->" + string);
                        handleSuccess((T) string, callback);
                    } else {
                        handleError("上传失败", callback);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 使用multipart/form-data格式上传带参数的文件，并且带上传进度
     *
     * @param <T>       the type parameter
     * @param actionUrl the action url
     * @param paramsMap the params map
     * @param callback  the callback
     */
    public <T> void uploadFile(String actionUrl, HashMap<String, Object> paramsMap, final ReqProgressCallback<T>
            callback) {
        try {
            //补全请求地址
            String requestUrl = String.format("%s/%s", HTTPS_BASE_URL, actionUrl);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置类型
            builder.setType(MultipartBody.FORM);
            //追加参数
            for (String key : paramsMap.keySet()) {
                Object object = paramsMap.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), createProgressRequestBody(MEDIA_OBJECT_STREAM, file,
                            callback));
                }
            }
            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = addHeaders().url(requestUrl).post(body).build();
            LOGD("request:" + request.toString()
                    + " headers:" + request.headers().toString()
                    + " body content type:" + request.body().contentType().toString());
            final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.toString());
                    handleError("上传失败", callback);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.e(TAG, "response ----->" + string);
                        handleSuccess((T) string, callback);
                    } else {
                        handleError("上传失败", callback);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 创建带进度的RequestBody
     *
     * @param <T>         the type parameter
     * @param contentType the content type
     * @param file        the file
     * @param callBack    the call back
     * @return the request body
     */
    private <T> RequestBody createProgressRequestBody(final MediaType contentType, final File file, final
    ReqProgressCallback<T> callBack) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    long remaining = contentLength();
                    long current = 0;
                    for (long readCount; (readCount = source.read(buf, 1)) != -1; ) {
                        sink.write(buf, readCount);
                        current += readCount;
                        Log.e(TAG, "current------>" + current);
                        progressCallBack(remaining, current, callBack);
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public <T> void downloadFile(String fileUrl, final String destFileDir, final HttpCallback<T> callback) {
        final File file = new File(destFileDir, fileUrl);
        if (file.exists()) {
            LOGD("file has existed");
        }

        Request request = addHeaders().url(HTTPS_BASE_URL + "/FileUpAndDown/download/" + fileUrl).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOGE("下载失败");
                handleError("下载失败", callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e(TAG, "current------>" + current);
                    }
                    fos.flush();
                    handleSuccess((T) file, callback);
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    handleError("下载失败", callback);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }

    public <T> void downloadFile(String fileUrl, final String destFileDir, final ReqProgressCallback<T> callback) {
        final File file = new File(destFileDir, fileUrl);
        if (file.exists()) {
            LOGD("file has existed");
        }

        Request request = addHeaders().url(HTTPS_BASE_URL + "/FileUpAndDown/download/" + fileUrl).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOGE("下载失败");
                handleError("下载失败", callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LOGD("response:" + response.toString() + "\n body:" + response.body());
                String header = response.header("Content-Disposition");
                String headers = response.headers().toString();
                LOGD("Content-Disposition:" + header + "\nheaders:" + headers);
                InputStream is = null;
                byte[] buf = new byte[2];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e(TAG, "current------>" + current + " buf:" + new String(buf));
                        progressCallBack(total, current, callback);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    fos.flush();
                    handleSuccess((T) file, callback);
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    handleError("下载失败", callback);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }

    public String register(String requestUrl, Employee employee) {
        StringBuilder sb = new StringBuilder();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, objectMapper.writeValueAsBytes(employee));
            LOGD("requestUrl:" + requestUrl);
            LOGD("employee:" + employee.toString());
            Request request = addHeaders().url(HTTPS_BASE_URL + requestUrl).post(requestBody).build();
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();
            String body = response.body().string();
            LOGD("status:" + response.code() + " response:" + body);
            LOGD("full response:" + response.toString());
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 统一为请求添加头信息
     *
     * @return
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE)
                .addHeader("appVersion", "1.0.0");
        return builder;
    }

    /**
     * 统一处理请求错误
     */
    private <T> void handleError(final String errorMsg, final HttpCallback<T> callback) {
        LOGE("handleError errorMsg:" + errorMsg);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onReqFail(errorMsg);
            }
        });
    }

    /**
     * 统一处理请求成功
     */
    private <T> void handleSuccess(final T result, final HttpCallback<T> callback) {
        LOGD("handleSuccess result:" + result);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onReqSuccess(result);
            }
        });
    }

    /**
     * 统一处理进度条信息
     */
    private <T> void progressCallBack(final long total, final long current, final ReqProgressCallback<T> callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(total, current);
                }
            }
        });
    }

    /**
     * Sets certificates.
     *
     * @param certificates the certificates
     */
    public SSLSocketFactory setCertificates(Context context, InputStream... certificates) {
        LOGD("setCertifacates");
        try {
            // 构造CertificateFactory对象，通过它的generateCertificate(is)方法得到Certificate
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                LOGD("certificate inputstream: " + certificate);
                String certificateAlias = Integer.toString(index++);
                // 然后将得到的Certificate放入到keyStore中
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                }
            }

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            // 接下来利用keyStore去初始化我们的TrustManagerFactory
            trustManagerFactory.init(keyStore);

            // 双向验证
            // 构造客户端keystore
            KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            clientKeyStore.load(context.getAssets().open("zhaocc_client.bks"), // 只能是bks格式
                    "zhao6735107".toCharArray());
            // 构造keyManagerFactory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm
                    ());
            keyManagerFactory.init(clientKeyStore, "zhao6735107".toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), // 客户端证书验证
                    trustManagerFactory.getTrustManagers(), // 服务端证书验证
                    new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void LOGD(String msg) {
        Log.d(TAG, msg);
    }

    private void LOGE(String msg) {
        Log.e(TAG, msg);
    }
}
