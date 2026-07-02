package com.rocketeers.nexus_gold.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadImage(MultipartFile file, String folder);
    void deleteImage(String publicUrlOrId);
}