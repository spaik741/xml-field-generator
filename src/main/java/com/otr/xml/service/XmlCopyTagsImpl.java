package com.otr.xml.service;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

@Component
public class XmlCopyTagsImpl implements XmlCopyTags {

    public String generateCopy(InputStream is, String genTag, List<String> uniqueTagList, int count) throws IOException {
        String xml = IOUtils.toString(is, StandardCharsets.UTF_8.name());
        // разбиваем на части xml, чтобы потом заново склеить
        // первая часть до окончания генерируемого тега
        StringBuilder generateXml = new StringBuilder(substringBefore(xml, finishTag(genTag)));
        generateXml.append(finishTag(genTag));
        // берем внутреннюю часть ген.тега, чтобы скопировать это n количество раз
        String innerTagText = substringBefore(substringAfter(xml, startTag(genTag)), finishTag(genTag));
        // указанное количество раз копируем и изменяем значения в уникальных тегов
        for (int i = 1; i <= count; i++) {
            generateXml.append(startTag(genTag));
            generateXml.append(generateUniqueVal(innerTagText, uniqueTagList, i));
            generateXml.append(finishTag(genTag));
        }
        generateXml.append(substringAfter(xml, finishTag(genTag)));
        return generateXml.toString();
    }

    private String generateUniqueVal(String text, List<String> uniqueTagList, int counter) {
        StringBuilder generateText = new StringBuilder();
        String subText = text;

//        for (int i = 0; i < uniqueTagList.size(); i++) {
//            String uniqueTag = uniqueTagList.get(i);
//            generateText.append(substringBefore(subText, startTag(uniqueTag)));
//            generateText.append(startTag(uniqueTag));
//            String innerTagText = substringBefore(substringAfter(subText, startTag(uniqueTag)), finishTag(uniqueTag));
//            generateText.append(Integer.parseInt(innerTagText) + counter);
//            generateText.append(finishTag(uniqueTag));
//            subText = substringAfter(subText, finishTag(uniqueTag));
//            if (i == uniqueTagList.size()){
//                generateText.append(subText);
//            }
//        }

        for (String uniqueTag : uniqueTagList) {
            generateText.append(substringBefore(subText, startTag(uniqueTag)));
            generateText.append(startTag(uniqueTag));
            String innerTagText = substringBefore(substringAfter(subText, startTag(uniqueTag)), finishTag(uniqueTag));
            generateText.append(Integer.parseInt(innerTagText) + counter);
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
