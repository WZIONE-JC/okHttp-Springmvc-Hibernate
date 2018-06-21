package com.baidu.zhaocc.okhttpdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.zhaocc.httpclient.HttpCallback;
import com.baidu.zhaocc.httpclient.ReqProgressCallback;
import com.baidu.zhaocc.httpclient.RequestManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {
    private final String TAG = MainActivityFragment.class.getSimpleName();
    private MyHandler mHandler = new MyHandler();
    private EditText recEt;
    private ProgressBar progressBar;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button getTestBt = view.findViewById(R.id.getTest);
        Button getTestAsyncBt = view.findViewById(R.id.getAsyncTest);
        Button postJsonSyncBt = view.findViewById(R.id.postJsonTest);
        Button postJsonAsyncBt = view.findViewById(R.id.postJsonAsyncTest);
        Button postFormSyncBt = view.findViewById(R.id.postFormTest);
        Button postFormAsyncBt = view.findViewById(R.id.postFormAsyncTest);
        Button uploadFile = view.findViewById(R.id.uploadFile);
        Button uploadFileWithParams = view.findViewById(R.id.uploadFileWithParam);
        Button uploadWithParaAndProg = view.findViewById(R.id.uploadFileWithParamAndProgress);
        progressBar = view.findViewById(R.id.progress);
        recEt = view.findViewById(R.id.received);
        getTestBt.setOnClickListener(this);
        getTestAsyncBt.setOnClickListener(this);
        postJsonSyncBt.setOnClickListener(this);
        postJsonAsyncBt.setOnClickListener(this);
        postFormSyncBt.setOnClickListener(this);
        postFormAsyncBt.setOnClickListener(this);
        uploadFile.setOnClickListener(this);
        uploadFileWithParams.setOnClickListener(this);
        uploadWithParaAndProg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick id:" + v.getId());
        switch (v.getId()) {
            case R.id.getTest:
                getTest();
                break;
            case R.id.getAsyncTest:
                getAsyncTest();
                break;
            case R.id.postJsonTest:
                postJsonSyncTest();
                break;
            case R.id.postJsonAsyncTest:
                postJsonAsyncTest();
                break;
            case R.id.postFormTest:
                postFormSyncTest();
                break;
            case R.id.postFormAsyncTest:
                postFormAsyncTest();
                break;
            case R.id.uploadFile:
                uploadFile();
                break;
            case R.id.uploadFileWithParam:
                uploadFileWithParam();
                break;
            case R.id.uploadFileWithParamAndProgress:
                uploadWithParaAndProg();
                break;
        }
    }

    private void getTest() {
        Log.d(TAG, "getTest");
        final HashMap<String, String> testMsg = new HashMap<>();
        testMsg.put("userName", "cc");
        testMsg.put("password", "pwd");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resStr = RequestManager.getInstance(getContext()).requestSync("simple/get", RequestManager.TYPE_GET, testMsg);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (resStr != null) {
                                recEt.setText(resStr);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    private void getAsyncTest() {
        Log.d(TAG, "getAsyncTest");
        final HashMap<String, String> testMsg = new HashMap<>();
        testMsg.put("userName", "cc");
        testMsg.put("password", "pwd");
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestManager.getInstance(getContext()).requestAsync("simple/get",
                        RequestManager.TYPE_GET, testMsg,
                        new HttpCallback<String>() {
                            @Override
                            public void onReqFail(String errorMsg) {
                                Log.e(TAG, "onReqFail errorMsg:" + errorMsg);
                            }

                            @Override
                            public void onReqSuccess(String result) {
                                Log.d(TAG, "onReqSuccess res:" + result);
                                recEt.setText(result);
                            }
                        });
            }
        }).start();
    }

    public void postJsonSyncTest() {
        Log.d(TAG, "postJsonSyncTest");
        final HashMap<String, String> testMsg = new HashMap<>();
        testMsg.put("userName", "cc");
        testMsg.put("password", "pwd");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resStr = RequestManager.getInstance(getContext()).requestSync("simple/post", RequestManager.TYPE_POST_JSON,
                        testMsg);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (resStr != null) {
                                recEt.setText(resStr);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

    }

    private void postJsonAsyncTest() {
        Log.d(TAG, "postJsonAsyncTest");
        final HashMap<String, String> testMsg = new HashMap<>();
        testMsg.put("userName", "cc");
        testMsg.put("password", "pwd");
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestManager.getInstance(getContext()).requestAsync("simple/post",
                        RequestManager.TYPE_POST_JSON, testMsg,
                        new HttpCallback<String>() {
                            @Override
                            public void onReqFail(String errorMsg) {
                                Log.e(TAG, "onReqFail errorMsg:" + errorMsg);
                            }

                            @Override
                            public void onReqSuccess(String result) {
                                Log.d(TAG, "onReqSuccess res:" + result);
                                recEt.setText(result);
                            }
                        });
            }
        }).start();
    }

    public void postFormSyncTest() {
        Log.d(TAG, "postFormSyncTest");
        final HashMap<String, String> testMsg = new HashMap<>();
        testMsg.put("userName", "cc");
        testMsg.put("password", "pwd");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resStr = RequestManager.getInstance(getContext()).requestSync("simple/post", RequestManager.TYPE_POST_FORM,
                        testMsg);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (resStr != null) {
                                recEt.setText(resStr);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

    }

    private void postFormAsyncTest() {
        Log.d(TAG, "postFormAsyncTest");
        final HashMap<String, String> testMsg = new HashMap<>();
        testMsg.put("userName", "cc");
        testMsg.put("password", "pwd");
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestManager.getInstance(getContext()).requestAsync("simple/post",
                        RequestManager.TYPE_POST_FORM, testMsg,
                        new HttpCallback<String>() {
                            @Override
                            public void onReqFail(String errorMsg) {
                                Log.e(TAG, "onReqFail errorMsg:" + errorMsg);
                            }

                            @Override
                            public void onReqSuccess(String result) {
                                Log.d(TAG, "onReqSuccess res:" + result);
                                recEt.setText(result);
                            }
                        });
            }
        }).start();
    }

    private void uploadFile() {
        File file = createFile();
        RequestManager.getInstance(getContext()).uploadFile("FileUpAndDown/upload", file.getAbsolutePath(), new HttpCallback<String>() {
            @Override
            public void onReqFail(String errorMsg) {
                Log.e(TAG, "onReqFail:" + errorMsg);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onReqSuccess(final String result) {
                Log.d(TAG, "onReqSuccess:" + result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        recEt.setText(result);
                        Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void uploadFileWithParam() {
        File file = createFile();
        HashMap<String, Object> params = new HashMap<>();
        params.put("description", file.getName());
        params.put("file", file);
        RequestManager.getInstance(getContext()).uploadFile("FileUpAndDown/upload", params, new HttpCallback<String>() {
            @Override
            public void onReqFail(String errorMsg) {
                Log.e(TAG, "onReqFail:" + errorMsg);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onReqSuccess(final String result) {
                Log.d(TAG, "onReqSuccess:" + result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        recEt.setText(result);
                        Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void uploadWithParaAndProg() {
        File file = createFile();
        HashMap<String, Object> params = new HashMap<>();
        params.put("description", file.getName());
        params.put("file", file);
        RequestManager.getInstance(getContext()).uploadFile("FileUpAndDown/upload", params, new ReqProgressCallback<String>() {
            @Override
            public void onReqFail(String errorMsg) {
                Log.e(TAG, "onReqFail:" + errorMsg);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onReqSuccess(final String result) {
                Log.d(TAG, "onReqSuccess:" + result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        recEt.setText(result);
                        Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(long total, long current) {
                final int progress = (int) (((float) current / (float) total) * 100.0);
                Log.d(TAG, "onProgress total:" + total + " current:" + current + " progress:" + progress);
                progressBar.setProgress(progress);
            }
        });
    }

    private File createFile() {
        try {
            File file = new File(getContext().getFilesDir(), "测试文件");
            if (!file.exists()) {
                file.createNewFile();
                OutputStream fos = new FileOutputStream(file);
                fos.write("this is a test file".getBytes());
                fos.close();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
        }
    }
}
