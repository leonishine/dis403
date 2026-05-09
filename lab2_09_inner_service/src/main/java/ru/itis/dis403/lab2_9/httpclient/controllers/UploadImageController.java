package ru.itis.dis403.lab2_9.httpclient.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.dis403.lab2_9.httpclient.service.ImageService;
import ru.itis.dis403.lab2_9.httpclient.service.ImageServiceForNats;

import java.io.IOException;
import java.util.*;

@Controller
public class UploadImageController {
    private final ImageService imageService;
    private final ImageServiceForNats imageService2;

    public UploadImageController(ImageService imageService, ImageServiceForNats imageService2) {
        this.imageService = imageService;
        this.imageService2 = imageService2;
    }

    @PostMapping(value = "/uploadimg", consumes="multipart/form-data")
    public String uploadImg(Model model, @RequestParam(value = "image", required = false) MultipartFile file) {
        model.addAttribute("imgs", new ArrayList<String>());

        try {
            if (file != null) {
                System.out.println("получили картинку");
                byte[] imageBytes = file.getBytes();
                imageService.processImage(imageBytes);
                imageService2.processImage(imageBytes);
            } else {
                System.out.println("нет изображения");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "uploadresult";
    }

    @GetMapping("/showimg")
    public String showImg(Model model) {
        List<String> imgs = imageService.getImgList();
        List<String> imgs2 = imageService2.getImgList();

        model.addAttribute("imgs", imgs != null ? imgs : new ArrayList<String>());
        model.addAttribute("imgs2", imgs2 != null ? imgs2 : new ArrayList<String>());

        List<Long> httpTimes = imageService.getResponseTimes();
        List<Long> natsTimes = imageService2.getResponseTimes();

        model.addAttribute("httpTimes", httpTimes);
        model.addAttribute("natsTimes", natsTimes);

        double avgHttp = httpTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double avgNats = natsTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

        model.addAttribute("avgHttp", String.format("%.2f", avgHttp));
        model.addAttribute("avgNats", String.format("%.2f", avgNats));

        return "uploadresult";
    }
}