package com.baidu.zhaocc.controller;

import com.baidu.zhaocc.dao.EmployeeDao;
import com.baidu.zhaocc.model.Employee;
import com.baidu.zhaocc.model.UserInfoModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.apache.log4j.lf5.viewer.LogFactor5Dialog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.util.Enumeration;

/**
 * @Auther: zhaochaochao
 * @Date: 2018/6/13
 * @Description:
 */
@Controller
@RequestMapping("/simple")
public class SimpleGetAndPostController {
    private static Logger logger = Logger.getLogger(FileUpAndDownController.class);

    @ModelAttribute("user")
    public UserInfoModel getUser() {
        return new UserInfoModel();
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public UserInfoModel doGet(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("doGet url:" + request.getRequestURI());
        Enumeration<?> headers = request.getHeaderNames();
        Object header = headers.nextElement();
        logger.debug("header: " + header);

        UserInfoModel user = new UserInfoModel("zhaocc", "qwe123");
        Cookie cookie = new Cookie("token", "This is token");
        cookie.setMaxAge(30 * 60); // 30 min
        cookie.setPath("/");
        response.addCookie(cookie);
        return user;
    }

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    @ResponseBody
    public String doPost(@ModelAttribute("user") UserInfoModel userInfo, HttpServletRequest request,
                         InputStream inputStream) {
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

    @RequestMapping(value = "/post", method = RequestMethod.POST, headers = "Content-Type=application/json")
    @ResponseBody
    public String register(HttpServletRequest request, InputStream inputStream) {
        try {
            logger.debug("doPost url:" + request.getRequestURI() + " contentType:" + request.getContentType());
            byte[] bytes = new byte[request.getContentLength()];
            inputStream.read(bytes);
            String str = new String(bytes, request.getCharacterEncoding());
            logger.debug("request body:" + str);
            ObjectMapper objectMapper = new ObjectMapper();
            Employee employee = objectMapper.readValue(bytes, Employee.class);
            logger.debug("employee:" + employee.toString());
            int id = EmployeeDao.save(employee);
            logger.debug("id:" + id);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
