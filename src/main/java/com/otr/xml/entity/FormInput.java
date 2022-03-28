package com.otr.xml.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FormInput {
    private String generationTag;
    private String uniqueTags;
    private String generationCount;
}
