package com.jun.reggie.controller;

import com.jun.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/*
 * @author cjj
 * */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    // 自定义路径，在yaml配置文件中
    // raggie:
    //  path: C:\Users\86156\Desktop\pic\
    @Value("${raggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @Transactional
    public R<String> fileUpload(MultipartFile file) {
        // 随机生成uuid
        String s = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        // 获取原文件后缀名
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        String path = s + substring;
        File dir = new File(path);
        try {
            // 目录是否存在
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 把文件存在服务器的哪里
            file.transferTo(new File(basePath + path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(path);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    // http://localhost:8080/common/download?name=7a4088a8-c762-4272-8146-0740b38e8d41.jpg
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        // 这里参数的名字实际上就是上传文件时的文件名，下载的时候直接使用这个名字进行下载
        try {
            // 从服务器读数据
            FileInputStream fis = new FileInputStream(new File(basePath + name));
            log.info(name);
            // 向浏览器写数据
            ServletOutputStream os = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fis.read(bytes)) != -1) {
                os.write(bytes,0,len);
            }
            fis.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
