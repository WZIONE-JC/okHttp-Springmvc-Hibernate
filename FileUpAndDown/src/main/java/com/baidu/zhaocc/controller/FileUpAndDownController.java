package com.baidu.zhaocc.controller;

import com.baidu.zhaocc.model.UserModel;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Auther: zhaochaochao
 * @Date: 2018/6/10
 * @Description:
 */
@Controller
@RequestMapping("/FileUpAndDown")
public class FileUpAndDownController {
    private static Logger logger = Logger.getLogger(FileUpAndDownController.class);

    @RequestMapping(value = "upload", method = RequestMethod.GET)
    public ModelAndView show() {
        return new ModelAndView("FileUploaderWithFormData");
    }

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST, headers = "Content-Type=application/octet-stream")
    public String upload(HttpServletRequest request, InputStream inputStream) throws Exception {
        logger.debug("upload with content-type is octet-stream");
        String path = request.getRealPath( "/files/");
        String fileName = request.getHeader("FileName");
        logger.debug("FileName:" + fileName);
        File filePath = new File(path, fileName);
        if (!filePath.getParentFile().exists()) {
            filePath.getParentFile().mkdirs();
        }
        OutputStream fos = new FileOutputStream(filePath);
        byte[] buffer = new byte[2048];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer, 0, 2048)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fos.close();
        inputStream.close();
        return "success";
    }

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
    public String upload(HttpServletRequest request,
                         @RequestParam("description") String description,
                         @RequestParam("file") MultipartFile file) throws Exception {
        logger.debug("upload with content-type is form-data");
        logger.debug("description:" + description);
        if (!file.isEmpty()) {
            String path = request.getRealPath( "/files/");
            logger.debug("path:" + path);
            String fileName = file.getOriginalFilename();
            File filePath = new File(path, fileName);
            if (!filePath.getParentFile().exists()) {
                filePath.getParentFile().mkdirs();
            }
            file.transferTo(filePath);
            return "success";
        }
        return "error";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegister() {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(HttpServletRequest request,
                           @ModelAttribute("user") UserModel userInfo) throws Exception {
        logger.debug("register");
        logger.debug("UserName:" + userInfo.getUserName());
        logger.debug("FileName:" + userInfo.getImage().getOriginalFilename());
        MultipartFile file = userInfo.getImage();
        if (!file.isEmpty()) {
            String path = request.getRealPath( "/images/");
            logger.debug("path:" + path);
            String fileName = file.getOriginalFilename();
            File filePath = new File(path, fileName);
            if (!filePath.getParentFile().exists()) {
                filePath.getParentFile().mkdirs();
            }
            file.transferTo(filePath);
            return "download";
        }
        return "error";
    }

    @RequestMapping(value =  "/download/")
    public ResponseEntity<byte[]> download(HttpServletRequest request,
                                           @RequestParam(value = "filename") String fileName) throws Exception {
        logger.debug("download filename:" + fileName);
        String path = request.getRealPath( "/images/");
        logger.debug("full file path:" + path + File.separator + fileName);
        File filePath = new File(path, fileName);
        if (filePath.exists()) {
            HttpHeaders headers = new HttpHeaders();
            // 解决中文乱码
            String downloadFielName = new String(fileName.getBytes("UTF-8"),"iso-8859-1");
            headers.setContentDispositionFormData("attachment", downloadFielName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(filePath),
                    headers, HttpStatus.CREATED);
        }
        return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value =  "/download/{filename:.*}")
    public ResponseEntity<byte[]> download2(HttpServletRequest request,
                                           @PathVariable(value = "filename") String fileName) throws Exception {
        logger.debug("download filename:" + fileName);
        String path = request.getRealPath( "/files/");
        logger.debug("full file path:" + path + File.separator + fileName);
        File filePath = new File(path, fileName);
        if (filePath.exists()) {
            HttpHeaders headers = new HttpHeaders();
            // 解决中文乱码
            String downloadFielName = new String(fileName.getBytes("UTF-8"),"iso-8859-1");
            headers.setContentDispositionFormData("attachment", downloadFielName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(filePath),
                    headers, HttpStatus.CREATED);
        }
        return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
