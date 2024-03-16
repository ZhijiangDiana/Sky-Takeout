package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.CommonService;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {
    @Autowired
    private CommonService commonService;

    @PostMapping("/upload")
    public Result<String> uploadFile(MultipartFile file) throws IOException {
        String url = commonService.uploadFile(file.getBytes(), file.getOriginalFilename());
        return Result.success(url);
    }
}
