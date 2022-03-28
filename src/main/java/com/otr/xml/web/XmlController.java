package com.otr.xml.web;

import com.otr.xml.entity.FormInput;
import com.otr.xml.service.XmlCopyTags;
import com.otr.xml.service.XmlCopyTagsImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class XmlController {

    @Autowired
    private XmlCopyTags xmlCopyTags;
    private String xml;

    @GetMapping("/")
    public String greetingForm(Model model) {
        model.addAttribute("formInput", new FormInput());
        model.addAttribute("text", "Тут будет результат импорта");
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @ModelAttribute FormInput formInput, Model model, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("text", "Ошибка отправки формы");
            return "index";
        }
        InputStream is = null;
        try {
            is = file.getInputStream();
            String genTag = formInput.getGenerationTag().trim();
            List<String> uniqueTagList = Arrays.stream(formInput.getUniqueTags().split(",")).map(String::trim).collect(Collectors.toList());
            int count = Integer.parseInt(formInput.getGenerationCount());
            String text = xmlCopyTags.generateCopy(is, genTag, uniqueTagList, count);
            model.addAttribute("text", text);
            xml = text;
        } catch (Exception e) {
            model.addAttribute("text", "Ошибка: " + e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                model.addAttribute("text", "Ошибка: " + e.getMessage());
            }
        }
        return "index";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(Model model) throws IOException {
        if (StringUtils.isEmpty(xml)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        HttpHeaders headers = new HttpHeaders();
        Resource downloadResource = new ByteArrayResource(xml.getBytes());
        headers.setContentType(MediaType.TEXT_XML);
        headers.setContentLength(downloadResource.contentLength());
        headers.setContentDispositionFormData("attachment", "file.xml");
        return new ResponseEntity<>(downloadResource, headers, HttpStatus.OK);
    }
}
