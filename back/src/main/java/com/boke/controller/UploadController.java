package com.boke.controller;

import com.boke.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${app.upload-path:./uploads}")
    private String uploadPath;

    @PostMapping
    public Result<List<Map<String, String>>> upload(@RequestParam("files") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Result.error(400, "请选择文件");
        }

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path dir = basePath.resolve(dateDir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            return Result.error(500, "创建上传目录失败");
        }

        List<Map<String, String>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                continue;
            }

            String ext = getExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;
            Path target = dir.resolve(filename);

            try {
                file.transferTo(target.toFile());
                String url = "/uploads/" + dateDir + "/" + filename;
                Map<String, String> item = new LinkedHashMap<>();
                item.put("url", url);
                item.put("name", file.getOriginalFilename());
                results.add(item);
            } catch (IOException e) {
                return Result.error(500, "文件上传失败: " + e.getMessage());
            }
        }

        return Result.success(results);
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int i = filename.lastIndexOf('.');
        return i >= 0 ? filename.substring(i).toLowerCase() : "";
    }
}
