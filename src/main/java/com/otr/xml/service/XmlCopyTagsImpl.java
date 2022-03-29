package com.otr.xml.service;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

@Component
public class XmlCopyTagsImpl implements XmlCopyTags {

    public List<String> generateCopyList(MultipartFile[] files, String genTag, List<String> uniqueTagList, int count) {
        List<String> xmlList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            InputStream is = null;
            try {
                is = files[i].getInputStream();
                String text = generateCopy(is, genTag, uniqueTagList, count, i);
                xmlList.add(text);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return xmlList;
    }

    public File toWrapUp(String fileName, List<String> xmlStrings) {
        FileOutputStream fos = null;
        ZipOutputStream zipOutputStream = null;
        File fileZip = new File(fileName);
        try {
            fos = new FileOutputStream(fileZip);
            zipOutputStream = new ZipOutputStream(fos);
            for (int i = 0; i < xmlStrings.size(); i++) {
                ZipEntry entry = new ZipEntry("notes_" + i + ".xml");
                zipOutputStream.putNextEntry(entry);
                zipOutputStream.write(xmlStrings.get(i).getBytes());
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
            zipOutputStream.close();
            fos.close();
        } catch (Exception e) {
            try {
                zipOutputStream.finish();
                zipOutputStream.close();
                fos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return fileZip;
    }

    public String generateCopy(InputStream is, String genTag, List<String> uniqueTagList, int countGen, int numFile) throws IOException {
        String xml = IOUtils.toString(is, StandardCharsets.UTF_8.name());
        // разбиваем на части xml, чтобы потом заново склеить
        // первая часть до окончания генерируемого тега
        StringBuilder generateXml = new StringBuilder(substringBefore(xml, finishTag(genTag)));
        generateXml.append(finishTag(genTag));
        // берем внутреннюю часть ген.тега, чтобы скопировать это n количество раз
        String innerTagText = substringBefore(substringAfter(xml, startTag(genTag)), finishTag(genTag));
        // указанное количество раз копируем и изменяем значения в уникальных тегов
        for (int i = 1; i <= countGen; i++) {
            generateXml.append(startTag(genTag));
            generateXml.append(generateUniqueVal(innerTagText, uniqueTagList, i, numFile));
            generateXml.append(finishTag(genTag));
        }
        generateXml.append(substringAfter(xml, finishTag(genTag)));
        return generateXml.toString();
    }

    private String generateUniqueVal(String text, List<String> uniqueTagList, int counter, int numFile) {
        StringBuilder generateText = new StringBuilder();
        String subText = text;

        for (String uniqueTag : uniqueTagList) {
            generateText.append(substringBefore(subText, startTag(uniqueTag)));
            generateText.append(startTag(uniqueTag));
            String innerTagText = substringBefore(substringAfter(subText, startTag(uniqueTag)), finishTag(uniqueTag));
            generateText.append(Integer.parseInt(innerTagText) + counter + numFile);
            generateText.append(finishTag(uniqueTag));
            subText = substringAfter(subText, finishTag(uniqueTag));
        }
        generateText.append(subText);
        return generateText.toString();
    }

    private String startTag(String t) {
        return "<" + t + ">";
    }

    private String finishTag(String t) {
        return "</" + t + ">";
    }
}
