package org.lyg.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author :lyg
 * @time :2018/7/29 0029
 */
@RestController
public class ResourceController {
    @RequestMapping("/download")
    public String download(HttpServletResponse response) throws IOException {
        File file = new File("e:/a.txt");
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition","attachment;fileName="+file.getName());
        OutputStream out = response.getOutputStream();
        FileUtils.copyFile(file,out);
        out.close();
        return "下载成功";
    }
    @RequestMapping("/inputStream")
    public byte[] download2() throws IOException {
        File file = new File("e:/a.txt");
        FileInputStream fin = new FileInputStream(file);
        byte[] bytes = new byte[102400];
        fin.read(bytes);
        return bytes;
    }
    @RequestMapping("/image")
    public void download3(HttpServletResponse response) throws IOException {
        File file = new File("e:/IMG_2878.JPG");
        FileInputStream fin = new FileInputStream(file);
        OutputStream out = response.getOutputStream();
        IOUtils.copy(fin,out);
    }
    @RequestMapping("/video")
    public void download4(HttpServletResponse response) throws IOException {
        File file = new File("e:/IMG_2886.mp4");
        FileInputStream fin = new FileInputStream(file);
        OutputStream out = response.getOutputStream();
        IOUtils.copy(fin,out);
    }
}
