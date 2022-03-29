package com.otr.xml.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class XmlCopyTagsTest {

    @Autowired
    private XmlCopyTags copyTags;

    @Test
    public void generateCopyTest() throws IOException {
        String xml = "<?xml version=\"1.0\"?>" +
                "<CAT>" +
                "<AGE>6</AGE>" +
                "</CAT>";
        String xml2 = "<?xml version=\"1.0\"?>" +
                "<CAT>" +
                "<AGE>6</AGE>" +
                "</CAT>" +
                "<CAT>" +
                "<AGE>7</AGE>" +
                "</CAT>";
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        String xmlGen = copyTags.generateCopy(is, "CAT", Collections.singletonList("AGE"), 1, 0);
        assertThat(xml2.equals(xmlGen));
    }

}