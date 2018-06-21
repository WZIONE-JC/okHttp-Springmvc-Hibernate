package com.baidu.zhaocc.controller;

import com.baidu.zhaocc.model.UserInfoModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * @Auther: zhaochaochao
 * @Date: 2018/6/13
 * @Description:
 */
@Controller
@RequestMapping("/simple")
public class SimpleGetAndPostController {
    private static Logger logger = Logger.getLogger(FileUpAndDownController.class);

    @ModelAttribute("user")        //① 暴露表单引用对象为模型数据
    public UserInfoModel getUser() {
        return new UserInfoModel();
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public UserInfoModel doGet(HttpServletRequest request) {
        logger.debug("doGet url:" + request.getRequestURI());
        UserInfoModel user = new UserInfoModel("zhaocc", "qwe123");
        return user;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getView() {
        return "userInfo";
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    @ResponseBody
    public String doPost(@ModelAttribute("user") UserInfoModel userInfo, HttpServletRequest request, InputStream inputStream) {
        try {
            logger.debug("doPost url:" + request.getRequestURI() + " contentType:" + request.getContentType());
            byte[] bytes = new byte[request.getContentLength()];
            inputStream.read(bytes);
            String str = new String(bytes, request.getCharacterEncoding());
            logger.debug("request body:" + str);
            logger.debug("userInfo:" + userInfo.toString());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
