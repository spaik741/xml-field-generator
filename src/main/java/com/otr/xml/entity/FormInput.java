package com.otr.xml.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FormInput {
    private String generationTag;
    private String uniqueTags;
    private String generationCount;

    public boolean isEmpty(){
        return StringUtils.isBlank(generationTag) || StringUtils.isBlank(uniqueTags) || StringUtils.isBlank(generationCount);
    }
}
