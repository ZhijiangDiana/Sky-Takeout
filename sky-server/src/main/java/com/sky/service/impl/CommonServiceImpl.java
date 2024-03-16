package com.sky.service.impl;

import com.sky.service.CommonService;
import com.sky.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private AliOssUtil aliOssUtil;
    @Override
    public String uploadFile(byte[] bytes, String originalFilename) {
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String storageFileName = UUID.randomUUID() + ext;
        return aliOssUtil.upload(bytes, storageFileName);
    }
}
