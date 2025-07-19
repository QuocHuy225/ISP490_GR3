package com.mycompany.isp490_gr3.util; // Hoặc package bạn muốn đặt

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Lớp này dạy cho Gson cách chuyển đổi giữa đối tượng LocalDate và chuỗi JSON.
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate date) throws IOException {
        if (date == null) {
            out.nullValue();
        } else {
            // Chuyển LocalDate thành chuỗi "YYYY-MM-DD"
            out.value(date.toString());
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            // Chuyển chuỗi "YYYY-MM-DD" từ JSON thành LocalDate
            return LocalDate.parse(in.nextString());
        }
    }
}