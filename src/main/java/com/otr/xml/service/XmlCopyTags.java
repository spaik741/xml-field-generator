package com.otr.xml.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface XmlCopyTags {
    String generateCopy(InputStream is, String genTag, List<String> uniqueTagList, int count , int numFile) throws IOException;
    List<String> generateCopyList(MultipartFile[] files, String genTag, List<String> uniqueTagList, int count);
    File toWrapUp(String fileName, List<String> xmlStrings);
}
