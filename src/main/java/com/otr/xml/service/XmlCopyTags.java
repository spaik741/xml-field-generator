package com.otr.xml.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface XmlCopyTags {
    String generateCopy(InputStream is, String genTag, List<String> uniqueTagList, int count) throws IOException;
}
