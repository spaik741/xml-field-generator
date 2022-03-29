package com.otr.xml.web;

import com.otr.xml.entity.FormInput;
import com.otr.xml.service.XmlCopyTags;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequiredArgsConstructor
public class XmlController {

    @Autowired
    private XmlCopyTags xmlCopyTags;
    @Autowired
    private ServletContext servletContext;
    private List<String> xmlStrings = new ArrayList<>();

    @GetMapping("/")
    public String greetingForm(Model model) {
        model.addAttribute("formInput", new FormInput());
        model.addAttribute("text", "Тут будет результат импорта");
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile[] files, @ModelAttribute FormInput formInput, Model model, BindingResult result) {
        if (result.hasErrors() || formInput.isEmpty()) {
            model.addAttribute("text", "Ошибка отправки формы");
            return "index";
        }

        String genTag = formInput.getGenerationTag().trim();
        List<String> uniqueTagList = Arrays.stream(formInput.getUniqueTags().split(",")).map(String::trim).collect(Collectors.toList());
        int count = Integer.parseInt(formInput.getGenerationCount());
        xmlStrings = xmlCopyTags.generateCopyList(files, genTag, uniqueTagList, count);
        model.addAttribute("text", "Количество готовых для экспорта файлов: " + xmlStrings.size());
        return "index";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() throws IOException {
        if (xmlStrings.size() < 1) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        HttpHeaders headers = new HttpHeaders();
        Resource downloadResource = new ByteArrayResource(xmlStrings.get(0).getBytes());
        downloadResource.getFile();
        headers.setContentType(MediaType.TEXT_XML);
        headers.setContentLength(downloadResource.contentLength());
        headers.setContentDispositionFormData("attachment", "file.xml");
        return new ResponseEntity<>(downloadResource, headers, HttpStatus.OK);
    }

    @GetMapping("/downloadZip")
    public ResponseEntity downloadZipFile(Model model) {
        String fileName = "zip_file.zip";
        if (xmlStrings.size() < 1) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        File fileZip = xmlCopyTags.toWrapUp(fileName, xmlStrings);
        String mineType = servletContext.getMimeType(fileName);
        MediaType mediaType = MediaType.parseMediaType(mineType);
        try {
            return ResponseEntity.ok()
                    .contentType(mediaType).body(new InputStreamResource((new ByteArrayInputStream(Files.readAllBytes(fileZip.toPath())))));
        } catch (IOException e) {
            model.addAttribute("text", "Ошибка при формировании архива: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("/clean")
    public String clean() {
        xmlStrings.clear();
        return "redirect:/";
    }
}
