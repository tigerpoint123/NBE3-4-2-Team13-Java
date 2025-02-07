package com.app.backend.global.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.springframework.data.domain.Page;

public class CustomPageModule extends SimpleModule {

    public CustomPageModule() {
        addSerializer(Page.class, new PageSerializer());
    }

    private static class PageSerializer extends StdSerializer<Page> {

        private PageSerializer() {
            super(Page.class);
        }

        @Override
        public void serialize(Page value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            gen.writeObjectField("content", value.getContent());
            gen.writeBooleanField("hasContent", value.hasContent());
            gen.writeNumberField("totalPages", value.getTotalPages());
            gen.writeNumberField("totalElements", value.getTotalElements());
            gen.writeNumberField("numberOfElements", value.getNumberOfElements());
            gen.writeNumberField("size", value.getSize());
            gen.writeNumberField("number", value.getNumber());
            gen.writeBooleanField("hasPrevious", value.hasPrevious());
            gen.writeBooleanField("hasNext", value.hasNext());
            gen.writeBooleanField("isFirst", value.isFirst());
            gen.writeBooleanField("isLast", value.isLast());
            gen.writeObjectField("sort", value.getSort());
            gen.writeEndObject();
        }

    }

}
