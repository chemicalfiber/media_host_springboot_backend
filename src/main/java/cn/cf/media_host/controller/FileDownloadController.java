package cn.cf.media_host.controller;

import cn.cf.media_host.service.MediaService;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class FileDownloadController {

    @Resource
    private MediaService mediaService;

    // 提供文件下载，根据文件ID下载文件
    @GetMapping("/f/{id}")
    public String download(@PathVariable String id, HttpServletResponse response) throws IOException {

        //查询文件
        GridFsResource gridFsResource = mediaService.downloadFile(id);

        String filename = gridFsResource.getFilename();
        System.out.println("filename: " + filename);

        response.reset();
//        response.setContentType(contentType);
        //注意: 如果没有下面这行设置header, 结果会将文件的内容作为响应的 body 直接输出在页面上, 而不是下载文件
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);  //指定下载文件名

        ServletOutputStream outputStream = response.getOutputStream();
        InputStream is = gridFsResource.getInputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = is.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
        }
        is.close();
        outputStream.close();

        return "OK";
    }
}
