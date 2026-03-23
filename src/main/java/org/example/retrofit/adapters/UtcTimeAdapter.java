package org.example.retrofit.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Custom Type Adapter for displaying all times in GMT.
 */
public class UtcTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
    public static final DateTimeFormatter CUSTOM_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
            .withZone(ZoneId.of("GMT"));

    @Override
    public ZonedDateTime deserialize(JsonElement dateTime, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            Instant instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(dateTime.getAsString()));
            return ZonedDateTime.ofInstant(instant, ZoneId.of("GMT"));
        } catch (Exception e) {
            throw new JsonParseException("Unparseable date: " + dateTime.getAsString(), e);
        }
    }

    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(CUSTOM_TIME_FORMATTER.format(src.toInstant()));
    }
}
