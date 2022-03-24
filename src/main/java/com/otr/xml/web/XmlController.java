package com.otr.xml.web;

import com.otr.xml.entity.FormInput;
import com.otr.xml.service.XmlCopyTags;
import com.otr.xml.service.XmlCopyTagsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class XmlController {

    @Autowired
    private XmlCopyTags xmlCopyTags;

    @GetMapping("/")
    public String greetingForm(Model model) {
        model.addAttribute("formInput", new FormInput());
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @ModelAttribute FormInput formInput, Model model, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("text", "Ошибка отправки формы");
            return "index";
        }
        try {
            InputStream is = file.getInputStream();
            String genTag = formInput.getGenerationTag().trim();
            List<String> uniqueTagList = Arrays.stream(formInput.getUniqueTags().split(",")).map(String::trim).collect(Collectors.toList());
            int count = Integer.parseInt(formInput.getGenerationCount());
            String text = xmlCopyTags.generateCopy(is, genTag, uniqueTagList, count);
            model.addAttribute("text", text);
        } catch (Exception e) {
            model.addAttribute("text", "Ошибка: " + e.getMessage());
        }
        return "index";
    }
}
