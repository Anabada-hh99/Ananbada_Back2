package com.example.advanced.domain;



import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
@Slf4j
public class PostCategoryConverter implements AttributeConverter<PostCategory, String> {
    @Override
    public String convertToDatabaseColumn(PostCategory postCategory) {
        if (postCategory == null)
            return null;
        return postCategory.getValue();
    }

    @Override
    public PostCategory convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;

        try {
            return PostCategory.fromCode(dbData);
        } catch (IllegalArgumentException e) {
            log.error("failure to convert cause unexpected code [{}]", dbData, e);
            throw e;
        }
    }
}
