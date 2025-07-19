package com.mycompany.isp490_gr3.util; // Hoặc package bạn đang dùng

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    // Định dạng chuẩn ISO, ví dụ: "2025-07-19T22:30:00"
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, LocalDateTime dateTime) throws IOException {
        if (dateTime == null) {
            out.nullValue();
        } else {
            out.value(FORMATTER.format(dateTime));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            return LocalDateTime.parse(in.nextString(), FORMATTER);
        }
    }
}