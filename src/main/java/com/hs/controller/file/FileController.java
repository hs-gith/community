package com.hs.controller.file;

import com.hs.pojo.file.FileInfo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author $author$
 * @Date $date$ $time$
 * @Description
 */
@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileController {
    /**
     * 文件上传
     * 根据fileType选择上传方式
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/files-anon")
    public FileInfo upload(@RequestParam("file") MultipartFile file) throws Exception {

        String fileType = FileType.QINIU.toString();
        FileService fileService = fileServiceFactory.getFileService(fileType);
        return fileService.upload(file);
    }

}
