package server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import data.Property;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> implements Property {
    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime dateTime) throws IOException {
        if (dateTime == null) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(dateTime.format(DATE_TIME_FORMATTER));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        JsonToken peek = jsonReader.peek();
        if (peek.name().equals("NULL")) {
            jsonReader.nextNull();
            return null;
        }
        return LocalDateTime.parse(jsonReader.nextString(), DATE_TIME_FORMATTER);
    }
}
