package com.mycompany.isp490_gr3.util; // Hoặc package bạn đang dùng

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Lớp này dạy cho Gson cách chuyển đổi giữa đối tượng LocalTime và chuỗi JSON.
 */
public class LocalTimeAdapter extends TypeAdapter<LocalTime> {

    // Định dạng giờ chuẩn HH:mm:ss
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    @Override
    public void write(JsonWriter out, LocalTime time) throws IOException {
        if (time == null) {
            out.nullValue();
        } else {
            out.value(FORMATTER.format(time));
        }
    }

    @Override
    public LocalTime read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            return LocalTime.parse(in.nextString(), FORMATTER);
        }
    }
}