package com.hf.healthfriend.global.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.hf.healthfriend.global.file.image.ImageExtension;

import java.io.IOException;

public class ImageExtensionDeserializer extends JsonDeserializer<ImageExtension> {

    @Override
    public ImageExtension deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String valueAsString = p.getValueAsString();
        return ImageExtension.valueOf(valueAsString.toUpperCase());
    }
}
